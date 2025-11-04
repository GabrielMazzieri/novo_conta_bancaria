package com.senai.novo_conta_bancaria.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Pagamento{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Conta conta;
    private String boleto;
    private BigDecimal valorPago;
    private LocalDateTime dataPagamento;
    private String status;

public void calcularValorFinal(BigDecimal valorFinal){
}

public void validarPagamento(){}

}
