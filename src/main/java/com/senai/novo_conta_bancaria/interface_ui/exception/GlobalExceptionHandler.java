package com.senai.novo_conta_bancaria.interface_ui.exception;

import com.senai.novo_conta_bancaria.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerConflitosGerais(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValoresNegativosException.class)
    public ProblemDetail handleValoresNegativos(ValoresNegativosException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Valores negativos não são permitidos.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ContaMesmoTipoException.class)
    public ProblemDetail handleContaMesmoTipo(ContaMesmoTipoException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.CONFLICT,
                "Não é permitido criar ou transferir para contas do mesmo tipo.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.NOT_FOUND,
                "A entidade solicitada não foi encontrada.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail handleSaldoInsuficiente(SaldoInsuficienteException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Saldo insuficiente para realizar a operação.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(TipoDeContaInvalidaException.class)
    public ProblemDetail handleTipoDeContaInvalida(TipoDeContaInvalidaException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Tipo de conta inválido.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(TransferirParaMesmaContaException.class)
    public ProblemDetail handleTransferirParaMesmaConta(TransferirParaMesmaContaException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Não é permitido transferir para a mesma conta.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(RendimentoInvalidoException.class)
    public ProblemDetail handleRendimentoInvalido(RendimentoInvalidoException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "O valor de rendimento informado é inválido.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail badRequest(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos são inválidos",
                request.getRequestURI()
        );

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        problem.setProperty("errors", errors);
        return problem;
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Tipo de parâmetro inválido");
        problem.setDetail(String.format(
                "O parâmetro '%s' deve ser do tipo '%s'. Valor recebido: '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido",
                ex.getValue()
        ));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ProblemDetail handleConversionFailed(ConversionFailedException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Falha de conversão de parâmetro");
        problem.setDetail("Um parâmetro não pôde ser convertido para o tipo esperado.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("error", ex.getMessage());
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Erro de validação nos parâmetros");
        problem.setDetail("Um ou mais parâmetros são inválidos");
        problem.setInstance(URI.create(request.getRequestURI()));

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String campo = violation.getPropertyPath().toString();
            String mensagem = violation.getMessage();
            errors.put(campo, mensagem);
        });
        problem.setProperty("errors", errors);
        return problem;
    }

}
