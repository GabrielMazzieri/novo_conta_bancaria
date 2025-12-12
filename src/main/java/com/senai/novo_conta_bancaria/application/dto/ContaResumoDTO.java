package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.entity.Cliente;
import com.senai.novo_conta_bancaria.domain.entity.Conta;
import com.senai.novo_conta_bancaria.domain.entity.ContaCorrente;
import com.senai.novo_conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.novo_conta_bancaria.domain.exception.TipoDeContaInvalidaException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Schema(description = "Representação resumida dos dados de uma conta")
public record ContaResumoDTO(
        @Schema(description = "Número da conta", example = "12345-6")
        @NotBlank(message = "O número da conta é obrigatório")
        String numero,

        @Schema(description = "Tipo da conta (CORRENTE ou POUPANCA)", example = "CORRENTE")
        @NotBlank(message = "O tipo da conta é obrigatório")
        String tipo,

        @Schema(description = "Saldo atual da conta", example = "100.00")
        @NotNull(message = "O saldo é obrigatório")
        @PositiveOrZero(message = "O saldo não pode ser negativo")
        BigDecimal saldo
) {

    public Conta toEntity(Cliente cliente) {
        if ("CORRENTE".equalsIgnoreCase(tipo)) {
            return ContaCorrente.builder()
                    .cliente(cliente)
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .taxa(new BigDecimal("0.05"))
                    .limite(new BigDecimal("500.00"))
                    .ativa(true)
                    .build();
        } else if ("POUPANCA".equalsIgnoreCase(tipo)) {
            return ContaPoupanca.builder()
                    .cliente(cliente)
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .rendimento(new BigDecimal("0.01"))
                    .ativa(true)
                    .build();
        }
        throw new TipoDeContaInvalidaException(tipo);
    }

    public static ContaResumoDTO fromEntity(Conta conta) {
        return new ContaResumoDTO(
                conta.getNumero(),
                conta.getTipo(),
                conta.getSaldo().setScale(2, RoundingMode.HALF_UP)
        );
    }
}
