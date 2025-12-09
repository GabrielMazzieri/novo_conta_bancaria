package com.senai.novo_conta_bancaria.infrastructure.mqtt;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import org.springframework.stereotype.Component;

@Component
public class BiometriaMqttPublisher {
    @MqttPublisher("banco/autenticacao/solicitacao")
    public String enviarSolicitacao(String jsonPayload) {
        return jsonPayload;
    }
}
