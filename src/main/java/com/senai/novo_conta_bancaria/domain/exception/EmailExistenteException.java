package com.senai.novo_conta_bancaria.domain.exception;

public class EmailExistenteException extends RuntimeException {
    public EmailExistenteException(String mensagem) {
        super(mensagem);
    }
}
