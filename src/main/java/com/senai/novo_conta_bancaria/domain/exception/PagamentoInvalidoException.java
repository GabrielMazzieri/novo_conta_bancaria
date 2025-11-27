package com.senai.novo_conta_bancaria.domain.exception;

public class PagamentoInvalidoException extends RuntimeException {
    public PagamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}