package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import lombok.Builder;

import java.math.BigDecimal;


public record ContaResumoDTO(
    String numero,
    String tipoConta,
    BigDecimal saldo
){
    public Conta toEntity(Cliente cliente) {
        if ("CORRENTE".equalsIgnoreCase(tipoConta)) {
            return ContaCorrente.builder()
                    .id(null)
                    .saldo(this.saldo)
                    .cliente(cliente)
                    .numero(this.numero)
                    .status(true)
                    .build();
        } else if ("POUPANCA".equalsIgnoreCase(tipoConta)) {
            return ContaPoupanca.builder()
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .cliente(cliente)
                    .status(true)
                    .build();
        } else {
            throw new IllegalArgumentException("Tipo de conta inválido: " + this.tipoConta);
    }

    public static Object fromEntity(Conta conta){
        return new ContaResumoDTO(
            conta.getNumero(),
            conta.getTipo(),
            conta.getSaldo()
            );
        }
    }
}
