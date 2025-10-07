package com.senai.conta_bancaria.domain.exception;

public class RendimentoInvalidoException extends RuntimeException {
    public RendimentoInvalidoException() {
        super("Rendimento deve ser aplicado somente em conta poupança!");
    }
}
