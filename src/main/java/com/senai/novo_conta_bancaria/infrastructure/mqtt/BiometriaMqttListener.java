package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper; // Adicione este import
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.novo_conta_bancaria.application.service.AutenticacaoIoTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BiometriaMqttListener {

    private final AutenticacaoIoTService autenticacaoService;
    private final ObjectMapper objectMapper;

    @MqttSubscriber("banco/validacao/biometria")
    public void receberConfirmacaoBiometria(@MqttPayload String payloadStr) {
        try {
            log.info(">>> LISTENER RECEBEU MENSAGEM BRUTA: {}", payloadStr);
            Map<String, Object> payload = objectMapper.readValue(payloadStr, Map.class);
            autenticacaoService.processarRespostaBiometria(payload);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem no Listener. Payload inválido: " + payloadStr, e);
        }
    }
}