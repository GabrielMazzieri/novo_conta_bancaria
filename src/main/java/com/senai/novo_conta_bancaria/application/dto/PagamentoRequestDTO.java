package com.senai.novo_conta_bancaria.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagamentoRequestDTO(
    @NotBlank(message = "O número da conta é obrigatório")
    String numeroConta,

    @NotBlank(message = "O código do boleto é obrigatório")
    String boleto,

    @NotNull(message = "O valor do pagamento é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal valor
) {}
