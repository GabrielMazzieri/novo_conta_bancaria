package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.enums.FormaPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PagamentoRequestDTO(
        @NotBlank(message = "O número da conta é obrigatório")
        String numeroConta,

        @NotBlank(message = "O código do boleto é obrigatório")
        String boleto,

        @NotNull(message = "O valor do pagamento é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "A data de vencimento é obrigatória")
        LocalDate dataVencimento,

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento
) {}