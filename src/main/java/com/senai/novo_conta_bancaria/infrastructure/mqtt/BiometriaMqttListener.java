package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.novo_conta_bancaria.application.service.AutenticacaoIoTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BiometriaMqttListener {

    private final AutenticacaoIoTService autenticacaoService;

    @MqttSubscriber("banco/validacao/+")
    public void receberConfirmacaoBiometria(@MqttPayload String payload) {
        log.info("Listener MQTT recebeu: {}", payload);
        autenticacaoService.processarRespostaBiometria(payload);
    }
}
