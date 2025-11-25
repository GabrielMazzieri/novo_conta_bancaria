package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.entity.Pagamento;
import com.senai.novo_conta_bancaria.domain.enums.PagamentoStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponseDTO(
    String id,
    String boleto,
    BigDecimal valorOriginal,
    BigDecimal valorFinal,
    LocalDateTime data,
    PagamentoStatus status
    ) {
    public static PagamentoResponseDTO fromEntity(Pagamento pagamento){
        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getBoleto(),
                pagamento.getValorPago(),
                pagamento.getValorPago(),
                pagamento.getDataPagamento(),
                pagamento.getStatus()
        );
    }


}
