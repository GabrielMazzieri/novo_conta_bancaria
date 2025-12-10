package com.senai.novo_conta_bancaria.application.dto;

import jakarta.validation.constraints.NotBlank;

public record DispositivoRegistroDTO(
        @NotBlank(message = "O código serial é obrigatório")
        String codigoSerial,
        @NotBlank(message = "A chave pública é obrigatória")
        String chavePublica
) {}
