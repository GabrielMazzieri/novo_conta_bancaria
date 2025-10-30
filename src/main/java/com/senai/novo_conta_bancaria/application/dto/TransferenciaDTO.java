package com.senai.novo_conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para realizar uma transferência")
public record TransferenciaDTO(
        @Schema(description = "Número da conta de destino", example = "98765-4")
        @NotBlank(message = "O número da conta de destino é obrigatório")
        String contaDestino,

        @Schema(description = "Valor a ser transferido", example = "150.00")
        @NotNull(message = "O valor da transferência é obrigatório")
        @Positive(message = "O valor da transferência deve ser maior que zero")
        BigDecimal valor
) {
}
