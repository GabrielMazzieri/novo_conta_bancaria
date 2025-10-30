package com.senai.novo_conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Schema(description = "Dados para atualização de uma conta (Admin/Gerente)")
public record ContaAtualizacaoDTO(
        @Schema(description = "Novo saldo da conta", example = "1500.00")
        @NotNull(message = "O saldo é obrigatório")
        @PositiveOrZero(message = "O saldo não pode ser negativo")
        BigDecimal saldo,

        @Schema(description = "Novo limite da conta corrente", example = "500.00")
        @NotNull(message = "O limite é obrigatório")
        @PositiveOrZero(message = "O limite não pode ser negativo")
        BigDecimal limite,

        @Schema(description = "Nova taxa de rendimento da conta poupança (ex: 0.01 para 1%)", example = "0.01")
        @NotNull(message = "O rendimento é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O rendimento deve ser maior que zero")
        BigDecimal rendimento,

        @Schema(description = "Nova taxa de saque/operação da conta corrente (ex: 0.05 para 5%)", example = "0.05")
        @NotNull(message = "A taxa é obrigatória")
        @DecimalMin(value = "0.0", inclusive = false, message = "A taxa deve ser maior que zero")
        BigDecimal taxa
) {
}
