package com.senai.conta_bancaria.application.dto;

import java.math.BigDecimal;

public record ContaAtualizacaoDTO(
        BigDecimal rendimento,
        BigDecimal limite,
        BigDecimal taxa,
        BigDecimal saldo
) {}
