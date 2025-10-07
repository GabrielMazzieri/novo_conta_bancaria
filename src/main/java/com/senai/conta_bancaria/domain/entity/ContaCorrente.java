package com.senai.conta_bancaria.domain.entity;

import com.senai.conta_bancaria.domain.exception.SaldoInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CORRENTE")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ContaCorrente extends Conta{

    @Column(precision = 19, scale = 2)
    private BigDecimal limite;

    @Column(precision = 19,scale = 2)
    private BigDecimal taxa;

    @Override
    public String getTipo() {
        return "CORRENTE";
    }

    @Override
    public void sacar(BigDecimal valor) {
       validarValorMaiorQueZero(valor,"saque");

        BigDecimal custoSaque = valor.multiply(taxa);
        BigDecimal totalSaque = valor.add(custoSaque);

        if(getSaldo().add(limite).compareTo(totalSaque)<0)
            throw new SaldoInsuficienteException("saque");

        setSaldo(getSaldo().subtract(totalSaque));
    }
}
