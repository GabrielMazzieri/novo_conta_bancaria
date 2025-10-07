package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.conta_bancaria.domain.exception.TipoDeContaInvalidaException;

import java.math.BigDecimal;

public record ContaResumoDTO(
        String numero,
        String tipo,
        BigDecimal saldo
) {

    public Conta toEntity(Cliente cliente) {
        if("CORRENTE".equalsIgnoreCase(tipo)) {
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
                conta.getSaldo()
        );
    }
}
