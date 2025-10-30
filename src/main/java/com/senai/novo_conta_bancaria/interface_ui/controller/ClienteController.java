package com.senai.novo_conta_bancaria.interface_ui.controller;

import com.senai.novo_conta_bancaria.application.dto.ClienteAtualizadoDTO;
import com.senai.novo_conta_bancaria.application.dto.ClienteRegistroDTO;
import com.senai.novo_conta_bancaria.application.dto.ClienteResponseDTO;
import com.senai.novo_conta_bancaria.application.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Clientes", description = "Gerenciamento de Clientes da conta bancaria")
@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @Operation(
            summary = "Registra um novo cliente",
            description = "Cadastra um novo cliente e sua primeira conta (Corrente ou Poupança).",
            requestBody = @RequestBody(
                    description = "Dados do cliente e da conta inicial",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ClienteRegistroDTO.class),
                            examples = {
                                    @ExampleObject(name = "Cliente com Conta Corrente", value = """
                                            {
                                              "nome": "Gabriel",
                                              "cpf": "12345678901",
                                              "email": "gabriel@email.com",
                                              "senha": "senha123",
                                              "conta": {
                                                "numero": "12345-6",
                                                "tipo": "CORRENTE",
                                                "saldo": 100.00
                                              }
                                            }
                                            """),
                                    @ExampleObject(name = "Cliente com Conta Poupança", value = """
                                            {
                                              "nome": "Lince",
                                              "cpf": "10987654321",
                                              "email": "lince@email.com",
                                              "senha": "senha456",
                                              "conta": {
                                                "numero": "98765-4",
                                                "tipo": "POUPANCA",
                                                "saldo": 500.00
                                              }
                                            }
                                            """)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente registrado com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Erro de validação (campos inválidos)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"O CPF deve conter exatamente 11 dígitos numéricos\""))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Cliente já possui conta deste tipo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"O Cliente já possui uma conta deste tipo.\""))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@Valid @org.springframework.web.bind.annotation.RequestBody ClienteRegistroDTO dto) {
        ClienteResponseDTO novoCliente = service.registrarCliente(dto);

        return ResponseEntity.created(URI.create("/api/cliente/cpf/" + novoCliente.cpf())
        ).body(novoCliente);
    }

    @Operation(
            summary = "Lista todos os clientes ativos",
            description = "Retorna uma lista de todos os clientes que não foram logicamente excluídos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesAtivos() {
        return ResponseEntity.ok(service.listarClientesAtivos());
    }

    @Operation(
            summary = "Busca um cliente ativo pelo CPF",
            description = "Retorna dados de um cliente específico (e suas contas) com base no CPF.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do cliente a ser buscado", example = "12345678901", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"cliente não existente ou inativo(a)!\""))
                    )
            }
    )
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarClienteAtivoPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(service.buscarClienteAtivoPorCpf(cpf));
    }

    @Operation(
            summary = "Atualiza os dados de um cliente",
            description = "Atualiza o nome e/ou CPF de um cliente existente.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do cliente a ser atualizado", example = "12345678901", required = true)
            },
            requestBody = @RequestBody(
                    description = "Novos dados do cliente",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ClienteAtualizadoDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Joao Silva Santos",
                                      "cpf": "12345678902"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação (campos inválidos)"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"cliente não existente ou inativo(a)!\""))
                    )
            }
    )
    @PutMapping("/{cpf}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable String cpf,
                                                               @Valid @org.springframework.web.bind.annotation.RequestBody ClienteAtualizadoDTO dto) {
        return ResponseEntity.ok(service.atualizarCliente(cpf, dto));
    }

    @Operation(
            summary = "Desativa um cliente",
            description = "Marca um cliente e todas as suas contas associadas como inativos.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do cliente a ser desativado", example = "12345678901", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente desativado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"cliente não existente ou inativo(a)!\""))
                    )
            }
    )
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deletarCliente(@PathVariable String cpf) {
        service.deletarCliente(cpf);
        return ResponseEntity.noContent().build();
    }
}
