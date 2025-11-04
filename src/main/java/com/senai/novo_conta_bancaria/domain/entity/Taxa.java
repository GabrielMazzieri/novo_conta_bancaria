package com.senai.novo_conta_bancaria.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Taxa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String descricao;
    private BigDecimal percentual;
    private BigDecimal valorFixo;

    public Taxa(){}

    public BigDecimal calcular(BigDecimal valorBase) {
        BigDecimal percentualValor = valorBase.multiply(percentual.divide(BigDecimal.valueOf(100)));
        return percentualValor.add(valorFixo != null ? valorFixo : BigDecimal.ZERO);
    }
}
