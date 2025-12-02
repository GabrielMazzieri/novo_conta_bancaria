package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import org.springframework.stereotype.Component;

@Component
public class MqttGateway {
    public void enviarSolicitacaoBiometria(String topico, String jsonPayload) {
        System.out.println("Enviando para tópico: " + topico + " | Payload: " + jsonPayload);
    }
}

