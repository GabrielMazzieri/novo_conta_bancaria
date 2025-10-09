package com.senai.conta_bancaria.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ContaAtualizacaoDTO(
        @NotNull(message = "O saldo é obrigatório")
        @PositiveOrZero(message = "O saldo não pode ser negativo")
        BigDecimal saldo,

        @NotNull(message = "O limite é obrigatório")
        @PositiveOrZero(message = "O limite não pode ser negativo")
        BigDecimal limite,

        @NotNull(message = "O rendimento é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O rendimento deve ser maior que zero")
        BigDecimal rendimento,

        @NotNull(message = "A taxa é obrigatória")
        @DecimalMin(value = "0.0", inclusive = false, message = "A taxa deve ser maior que zero")
        BigDecimal taxa
) {
}
