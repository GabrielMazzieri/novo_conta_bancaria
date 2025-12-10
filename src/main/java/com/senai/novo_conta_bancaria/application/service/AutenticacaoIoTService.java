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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;

    private final Map<String, CountDownLatch> travasDeEspera = new ConcurrentHashMap<>();

    public void solicitarAutenticacaoBiometrica(Cliente cliente) {
        DispositivoIoT dispositivo = dispositivoRepository.findByClienteIdAndAtivoTrue(cliente.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Dispositivo IoT não encontrado ou inativo."));

        String codigoGerado = String.format("%06d", new Random().nextInt(999999));

        CodigoAutenticacao novoCodigo = transactionTemplate.execute(status -> {
            CodigoAutenticacao cod = CodigoAutenticacao.builder()
                    .codigo(codigoGerado)
                    .expiraEm(LocalDateTime.now().plusSeconds(180))
                    .validado(false)
                    .cliente(cliente)
                    .build();
            return codigoRepository.saveAndFlush(cod);
        });

        CountDownLatch latch = new CountDownLatch(1);
        travasDeEspera.put(cliente.getId(), latch);

        try {
            String jsonPayload = String.format("{\"clienteId\":\"%s\", \"acao\":\"SOLICITAR_BIOMETRIA\", \"codigo\":\"%s\"}",
                    cliente.getId(), codigoGerado);

            log.info("Enviando solicitação MQTT para cliente {}. Código: {}. Aguardando resposta...", cliente.getId(), codigoGerado);

            mqttPublisher.enviarSolicitacao(jsonPayload);

            boolean recebeuResposta = latch.await(120, TimeUnit.SECONDS);

            if (!recebeuResposta) {
                log.error("Timeout: Nenhuma resposta recebida do dispositivo para o cliente {}", cliente.getId());
                throw new AutenticacaoIoTExpiradaException();
            }

            CodigoAutenticacao codigoAtualizado = transactionTemplate.execute(status -> {
                CodigoAutenticacao cod = codigoRepository.findById(novoCodigo.getId())
                        .orElseThrow(AutenticacaoIoTExpiradaException::new);

            entityManager.refresh(cod);

            return cod;

            });

            if (!codigoAtualizado.isValidado()) {
                log.warn("O código foi encontrado, mas o status 'validado' ainda é false.");
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

    public void processarRespostaBiometria(Map<String, Object> dados) {
        try {
            String clienteId = (String) dados.get("clienteId");
            String codigoRecebido = String.valueOf(dados.get("codigo"));

            log.info("Recebido MQTT -> Cliente: {}, Código: {}", clienteId, codigoRecebido);

            if (clienteId == null || codigoRecebido == null || "null".equals(codigoRecebido)) {
                log.warn("Payload incompleto recebido.");
                return;
            }

            var codigoOptional = codigoRepository.findFirstByClienteIdAndValidadoFalseOrderByExpiraEmDesc(clienteId);

            if (codigoOptional.isEmpty()) {
                log.error("ERRO GRAVE: O código enviado pelo MQTT não foi encontrado no banco! " +
                        "Cliente: {}. Verifique se o TransactionTemplate funcionou.", clienteId);
                return;
            }

            CodigoAutenticacao codigoBanco = codigoOptional.get();

            if (codigoBanco.getCodigo().equals(codigoRecebido) && codigoBanco.getExpiraEm().isAfter(LocalDateTime.now())) {

                codigoBanco.setValidado(true);
                codigoRepository.saveAndFlush(codigoBanco);

                log.info("Código validado corretamente! Liberando a thread principal...");

                if (travasDeEspera.containsKey(clienteId)) {
                    travasDeEspera.get(clienteId).countDown();
                } else {
                    log.warn("Nenhuma thread esperando por este cliente. Talvez tenha ocorrido timeout antes.");
                }
            } else {
                log.warn("Código inválido ou expirado. Banco: {}, Recebido: {}", codigoBanco.getCodigo(), codigoRecebido);
            }

        } catch (Exception e) {
            log.error("Erro desconhecido no processamento MQTT", e);
        }
    }
}