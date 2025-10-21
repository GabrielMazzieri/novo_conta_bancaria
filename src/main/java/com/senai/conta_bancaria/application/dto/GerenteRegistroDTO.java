package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.Gerente;
import com.senai.conta_bancaria.domain.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record GerenteRegistroDTO(
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Email
        @NotNull(message = "O email não pode ser nulo.")
        @NotBlank(message = "O email não pode ser vazio")
        String email,

        @NotNull(message = "A senha não pode ser nulo.")
        @NotBlank(message = "A senha não pode ser vazio")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
        String senha,

        @NotNull(message = "As informações da conta são obrigatórias")
        @Valid ContaResumoDTO conta
) {
    public Gerente toEntity() {
        return Gerente.builder()
                .ativo(true)
                .email(email)
                .senha(senha)
                .nome(nome)
                .cpf(cpf)
                .role(Role.GERENTE)
                .build();
    }
}
