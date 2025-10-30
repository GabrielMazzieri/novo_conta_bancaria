package com.senai.novo_conta_bancaria.domain.exception;

public class ContaMesmoTipoException extends RuntimeException {
  public ContaMesmoTipoException() {
    super("O Cliente já possui uma conta deste tipo.");
  }
}
