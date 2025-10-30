package com.senai.novo_conta_bancaria.domain.exception;

public class TransferirParaMesmaContaException extends RuntimeException {
  public TransferirParaMesmaContaException() {
    super("Não é possível transferir para a mesma conta.");
  }
}
