package com.senai.novo_conta_bancaria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;

@Data
@Entity
public class Taxa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal percentual;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valorFixo;

    @ManyToMany(mappedBy = "taxas", fetch = FetchType.LAZY)
    private HashSet<Pagamento> pagamentos;

    public BigDecimal calcular(BigDecimal valorBase) {
        BigDecimal percentualValor = valorBase.multiply(percentual.divide(BigDecimal.valueOf(100)));
        return percentualValor.add(valorFixo != null ? valorFixo : BigDecimal.ZERO);
    }
}
