package com.senai.novo_conta_bancaria.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record TaxaRequestDTO(
        @NotBlank(message = "A descrição da taxa é obrigatória")
        String descricao,

        @NotNull(message = "O percentual é obrigatório")
        @PositiveOrZero(message = "O percentual não pode ser negativo")
        BigDecimal percentual,

        @PositiveOrZero(message = "O valor fixo não pode ser negativo")
        BigDecimal valorFixo
) {
}
