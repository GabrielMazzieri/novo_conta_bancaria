package com.senai.novo_conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para operações de saque ou depósito")
public record ValorSaqueDepositoDTO(
        @Schema(description = "Valor da operação", example = "100.00")
        @NotNull(message = "O valor é obrigatório")
        BigDecimal valor
) {
}

