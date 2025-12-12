package com.senai.novo_conta_bancaria.infrastructure.mqtt;

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

    @MqttSubscriber("banco/validacao/biometria")
    public void receberConfirmacaoBiometria(@MqttPayload Map<String, Object> payload) {
        try {
            log.info(">>> LISTENER RECEBEU MENSAGEM: {}", payload);
            autenticacaoService.processarRespostaBiometria(payload);
        } catch (Exception e) {
            log.error("Erro ao processar mensagem no Listener: ", e);
        }
    }
}