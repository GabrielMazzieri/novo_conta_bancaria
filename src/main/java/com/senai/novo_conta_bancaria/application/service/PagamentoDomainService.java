package com.senai.novo_conta_bancaria.application.service;

import com.senai.novo_conta_bancaria.domain.entity.Conta;
import com.senai.novo_conta_bancaria.domain.entity.Taxa;
import com.senai.novo_conta_bancaria.domain.exception.PagamentoInvalidoException;
import com.senai.novo_conta_bancaria.domain.exception.SaldoInsuficienteException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Service
public class PagamentoDomainService {

    public BigDecimal calcularValorFinal(BigDecimal valorBoleto, Set<Taxa> taxas){
        BigDecimal valorAcumulado = valorBoleto;

        if (taxas != null && !taxas.isEmpty()){
            for (Taxa taxa : taxas){
                valorAcumulado = valorAcumulado.add(taxa.calcular(valorBoleto));
            }
        }
        return valorAcumulado;
    }

    public void validarSaldoSuficiente(Conta conta, BigDecimal valorFinal){
        BigDecimal saldoDisponivel = conta.getSaldo();

        if (saldoDisponivel.compareTo(valorFinal) < 0){
            throw new SaldoInsuficienteException("pagamento de boleto");
        }
    }

    public void validarVencimento(LocalDate dataVencimento) {
        if (dataVencimento.isBefore(LocalDate.now())) {
            throw new PagamentoInvalidoException("O boleto está vencido. Vencimento: " + dataVencimento);
        }
    }
}
