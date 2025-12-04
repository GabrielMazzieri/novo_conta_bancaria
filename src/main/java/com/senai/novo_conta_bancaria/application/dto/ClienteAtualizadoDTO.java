package com.senai.novo_conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização de um cliente (Nome e CPF)")
public record ClienteAtualizadoDTO(
        @Schema(description = "Nome completo do cliente", example = "Joao Silva Santos")
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Schema(description = "Email do cliente", example = "gabriel@gmail.com")
        @NotNull(message = "O email não pode ser nulo.")
        @NotBlank(message = "O email não pode ser vazio.")
        String email,

        @Schema(description = "Senha do cliente", example = "12345678@")
        @NotNull(message = "A senha não pode ser nula.")
        @NotBlank(message = "A senha não pode ser vazia.")
        String senha
) {
}