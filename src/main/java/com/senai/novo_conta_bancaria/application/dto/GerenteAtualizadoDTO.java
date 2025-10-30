package com.senai.novo_conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados para atualização de um gerente")
public record GerenteAtualizadoDTO(
        @Schema(description = "Nome completo do gerente", example = "Maria Gerente Silva")
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Schema(description = "Número do CPF do gerente (apenas dígitos)", example = "98765432101")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Email de login do gerente", example = "maria.silva@banco.com")
        @Email
        @NotNull(message = "O e-mail não pode ser nulo.")
        @NotBlank(message = "O e-mail não pode ser vazio.")
        String email,

        @Schema(description = "Nova senha de login do gerente (mínimo 8 caracteres)", example = "novasenha123")
        @NotNull(message = "A senha não pode ser nula.")
        @NotBlank(message = "A senha não pode ser vazia.")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String senha
) {
}
