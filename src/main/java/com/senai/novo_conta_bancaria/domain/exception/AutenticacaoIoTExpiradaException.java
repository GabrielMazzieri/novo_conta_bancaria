package com.senai.novo_conta_bancaria.domain.exception;

public class AutenticacaoIoTExpiradaException extends RuntimeException {
    public AutenticacaoIoTExpiradaException() {
        super("A autenticação biométrica expirou ou falhou. Tente novamente.");
    }
}
