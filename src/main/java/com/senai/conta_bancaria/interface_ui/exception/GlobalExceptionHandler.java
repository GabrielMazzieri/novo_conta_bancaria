package com.senai.conta_bancaria.interface_ui.exception;

import com.senai.conta_bancaria.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValoresNegativosException.class)
    public ResponseEntity<String> handleValoresNegativos(ValoresNegativosException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContaMesmoTipoException.class)
    public ResponseEntity<String> handleContaMesmoTipo(ContaMesmoTipoException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<String> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TipoDeContaInvalidaException.class)
    public ResponseEntity<String> handleTipoDeContaInvalida(TipoDeContaInvalidaException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValoresNegativosException.class)
    public ResponseEntity<String> handleContaMesmoTipo(ContaMesmoTipoException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValoresNegativosException.class)
    public ResponseEntity<String> handleContaMesmoTipo(ContaMesmoTipoException ex) {
        return new ResponseEntity <>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
