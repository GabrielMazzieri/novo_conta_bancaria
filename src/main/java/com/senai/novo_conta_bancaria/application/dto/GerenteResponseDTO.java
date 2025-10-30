package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.entity.Cliente;
import com.senai.novo_conta_bancaria.domain.entity.Gerente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "Dados de resposta para um gerente cadastrado")
public record GerenteResponseDTO(
        @Schema(description = "ID único do gerente (UUID)", example = "b2c3d4e5-f6g7-8901-h2i3-j4k5l6m7n8o9")
        String id,

        @Schema(description = "Nome completo do gerente", example = "Maria Gerente")
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Schema(description = "Número do CPF do gerente (apenas dígitos)", example = "98765432100")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Email de login do gerente", example = "maria.gerente@banco.com")
        @Email
        @NotNull(message = "O e-mail não pode ser nulo.")
        @NotBlank(message = "O e-mail não pode ser vazio.")
        String email,

        @Schema(description = "Senha (hash) do gerente", example = "$2a$10$...")
        @NotNull(message = "A senha não pode ser nula.")
        @NotBlank(message = "A senha não pode ser vazia.")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String senha
) {
    public static GerenteResponseDTO fromEntity(Gerente gerente) {
        return new GerenteResponseDTO(
                gerente.getId(),
                gerente.getNome(),
                gerente.getCpf(),
                gerente.getEmail(),
                gerente.getSenha()
        );
    }
}
