package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import org.springframework.stereotype.Component;

@Component
public class MqttGateway {
    @MqttPublisher("banco/autenticacao/solicitacao")
    public String enviarSolicitacaoBiometria(String jsonPayload) {
        return jsonPayload;
    }
}

