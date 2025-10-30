package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.ArrayList;


@Builder
@Schema(description = "Dados para registro de um novo cliente e sua conta inicial")
public record ClienteRegistroDTO(
        @Schema(description = "Nome completo do cliente", example = "Joao Silva")
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Schema(description = "Número do CPF do cliente (apenas dígitos)", example = "12345678901")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Email para login do cliente", example = "joao.silva@email.com")
        @Email
        @NotNull(message = "O email não pode ser nulo.")
        @NotBlank(message = "O email não pode ser vazio")
        String email,

        @Schema(description = "Senha para login do cliente (mínimo 8 caracteres)", example = "senha1234")
        @NotNull(message = "A senha não pode ser nulo.")
        @NotBlank(message = "A senha não pode ser vazio")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String senha,

        @Schema(description = "Dados da primeira conta (Corrente ou Poupança)")
        @NotNull(message = "As informações da conta são obrigatórias")
        @Valid ContaResumoDTO conta
) {
    public Cliente toEntity() {
        return Cliente.builder()
                .ativo(true)
                .email(email)
                .senha(senha)
                .nome(nome)
                .cpf(cpf)
                .contas(new ArrayList<Conta>())
                .role(Role.CLIENTE)
                .build();
    }
}
