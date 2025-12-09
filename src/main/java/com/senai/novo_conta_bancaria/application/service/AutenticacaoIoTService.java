package com.senai.novo_conta_bancaria.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.novo_conta_bancaria.domain.entity.Cliente;
import com.senai.novo_conta_bancaria.domain.entity.CodigoAutenticacao;
import com.senai.novo_conta_bancaria.domain.entity.DispositivoIoT;
import com.senai.novo_conta_bancaria.domain.exception.AutenticacaoIoTExpiradaException;
import com.senai.novo_conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.novo_conta_bancaria.domain.repository.CodigoAutenticacaoRepository;
import com.senai.novo_conta_bancaria.domain.repository.DispositivoIoTRepository;
import com.senai.novo_conta_bancaria.infrastructure.mqtt.BiometriaMqttPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutenticacaoIoTService {

    private final DispositivoIoTRepository dispositivoRepository;
    private final CodigoAutenticacaoRepository codigoRepository;
    private final BiometriaMqttPublisher mqttPublisher;
    private final ObjectMapper objectMapper;

    private final Map<String, CountDownLatch> travasDeEspera = new ConcurrentHashMap<>();

    public void solicitarAutenticacaoBiometrica(Cliente cliente) {
        DispositivoIoT dispositivo = dispositivoRepository.findByClienteIdAndAtivoTrue(cliente.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Dispositivo IoT não encontrado ou inativo."));

        String codigoGerado = String.format("%06d", new Random().nextInt(999999));
        CodigoAutenticacao novoCodigo = CodigoAutenticacao.builder()
                .codigo(codigoGerado)
                .expiraEm(LocalDateTime.now().plusSeconds(60))
                .validado(false)
                .cliente(cliente)
                .build();
        codigoRepository.save(novoCodigo);

        CountDownLatch latch = new CountDownLatch(1);
        travasDeEspera.put(cliente.getId(), latch);

        try {
            String jsonPayload = String.format("{\"clienteId\":\"%s\", \"acao\":\"SOLICITAR_BIOMETRIA\", \"codigo\":\"%s\"}",
                    cliente.getId(), codigoGerado);

            log.info("Enviando solicitação MQTT para cliente {}. Aguardando resposta...", cliente.getId());

            mqttPublisher.enviarSolicitacao(jsonPayload);

            boolean recebeuResposta = latch.await(40, TimeUnit.SECONDS);

            if (!recebeuResposta) {
                log.error("Timeout: Nenhuma resposta recebida do dispositivo para o cliente {}", cliente.getId());
                throw new AutenticacaoIoTExpiradaException();
            }

            CodigoAutenticacao codigoAtualizado = codigoRepository.findById(novoCodigo.getId())
                    .orElseThrow(AutenticacaoIoTExpiradaException::new);

            if (!codigoAtualizado.isValidado()) {
                throw new AutenticacaoIoTExpiradaException();
            }

            log.info("Autenticação biométrica confirmada com sucesso!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processo de autenticação interrompido.", e);
        } finally {
            travasDeEspera.remove(cliente.getId());
        }
    }

    public void processarRespostaBiometria(String payload) {
        try {
            Map<String, String> dados = objectMapper.readValue(payload, Map.class);
            String clienteId = dados.get("clienteId");
            String codigoRecebido = dados.get("codigo");

            if (clienteId == null || codigoRecebido == null) {
                log.warn("Payload MQTT inválido ou incompleto: {}", payload);
                return;
            }

            codigoRepository.findFirstByClienteIdAndValidadoFalseOrderByExpiraEmDesc(clienteId)
                    .ifPresent(codigoBanco -> {
                        if (codigoBanco.getCodigo().equals(codigoRecebido)
                                && codigoBanco.getExpiraEm().isAfter(LocalDateTime.now())) {

                            codigoBanco.setValidado(true);
                            codigoRepository.save(codigoBanco);

                            log.info("Código validado corretamente para cliente {}. Liberando thread...", clienteId);

                            if (travasDeEspera.containsKey(clienteId)) {
                                travasDeEspera.get(clienteId).countDown();
                            }
                        } else {
                            log.warn("Código recebido incorreto ou expirado para cliente {}", clienteId);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Erro ao fazer parse do JSON recebido via MQTT", e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar resposta de biometria", e);
        }
    }
}