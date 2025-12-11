package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper; // Importante
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispositivoVirtualService {

    private final BiometriaMqttPublisher publisher;
    private final ObjectMapper objectMapper;

    @MqttSubscriber("banco/autenticacao/solicitacao")
    public void simularClienteColocandoDedo(@MqttPayload String payloadStr) {
        log.info("============== [DISPOSITIVO VIRTUAL] ==============");
        log.info("🤖 Payload BRUTO recebido: {}", payloadStr);

        try {
            Map<String, Object> payload = objectMapper.readValue(payloadStr, Map.class);

            log.info("✅ JSON Convertido: {}", payload);

            CompletableFuture.runAsync(() -> {
                try {
                    log.info("⏳ Simulando leitura biométrica (5 segundos)...");
                    TimeUnit.SECONDS.sleep(5);

                    String clienteId = (String) payload.get("clienteId");
                    String codigo = (String) payload.get("codigo");

                    String jsonResposta = String.format(
                            "{\"clienteId\":\"%s\", \"codigo\":\"%s\", \"status\":\"SUCESSO\"}",
                            clienteId, codigo
                    );

                    log.info("📤 Enviando resposta para o banco...");
                    publisher.enviarRespostaValidacao(jsonResposta);

                } catch (Exception e) {
                    log.error("Erro na simulação do dispositivo", e);
                }
            });

        } catch (Exception e) {
            log.error("❌ Erro ao ler JSON no dispositivo virtual: " + payloadStr, e);
        }
    }
}