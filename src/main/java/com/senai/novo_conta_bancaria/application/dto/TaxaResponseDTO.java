package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.entity.Taxa;
import com.senai.novo_conta_bancaria.domain.enums.FormaPagamento;

import java.math.BigDecimal;

public record TaxaResponseDTO(
    String id,
    String descricao,
    BigDecimal percentual,
    BigDecimal valorFixo,
    FormaPagamento tipoPagamento
) {
    public static TaxaResponseDTO fromEntity(Taxa taxa) {
        return new TaxaResponseDTO(
            taxa.getId(),
            taxa.getDescricao(),
            taxa.getPercentual(),
            taxa.getValorFixo(),
            taxa.getTipoPagamento()
        );
    }
}
