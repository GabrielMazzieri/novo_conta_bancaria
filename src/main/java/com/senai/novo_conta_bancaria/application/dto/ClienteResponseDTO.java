package com.senai.novo_conta_bancaria.application.dto;

import com.senai.novo_conta_bancaria.domain.entity.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Dados de resposta para um cliente cadastrado")
public record ClienteResponseDTO(
        @Schema(description = "ID único do cliente (UUID)", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
        @NotBlank(message = "O ID do cliente é obrigatório")
        String id,

        @Schema(description = "Nome completo do cliente", example = "Joao Silva")
        @NotNull(message = "O nome não pode ser nulo.")
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Schema(description = "Número do CPF do cliente (apenas dígitos)", example = "12345678901")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Lista de contas associadas ao cliente")
        List<ContaResumoDTO> contas
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        List<ContaResumoDTO> contas = cliente.getContas().stream()
                .map(ContaResumoDTO::fromEntity)
                .toList();

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                contas
        );
    }
}
