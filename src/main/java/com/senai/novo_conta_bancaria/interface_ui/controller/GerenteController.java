package com.senai.novo_conta_bancaria.interface_ui.controller;

import com.senai.novo_conta_bancaria.application.dto.GerenteAtualizadoDTO;
import com.senai.novo_conta_bancaria.application.dto.GerenteRegistroDTO;
import com.senai.novo_conta_bancaria.application.dto.GerenteResponseDTO;
import com.senai.novo_conta_bancaria.application.service.GerenteService;
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

@Tag(name = "Gerentes", description = "Gerenciamento de Gerentes da Conta Bancaria")
@RestController
@RequestMapping("/api/gerente")
@RequiredArgsConstructor
public class GerenteController {
    private final GerenteService service;

    @Operation(
            summary = "Registra um novo gerente",
            description = "Cadastra um novo gerente no sistema.",
            requestBody = @RequestBody(
                    description = "Dados do gerente a ser registrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GerenteRegistroDTO.class),
                            examples = @ExampleObject(name = "Exemplo Gerente", value = """
                                    {
                                      "nome": "Maria Gerente",
                                      "cpf": "98765432100",
                                      "email": "maria.gerente@banco.com",
                                      "senha": "senhaforte123",
                                      "conta": {
                                        "numero": "00000-0",
                                        "tipo": "CORRENTE",
                                        "saldo": 0.0
                                      }
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Gerente registrado com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Erro de validação (campos inválidos)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"O CPF deve conter exatamente 11 dígitos numéricos\""))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<GerenteResponseDTO> registrarGerente(@Valid @org.springframework.web.bind.annotation.RequestBody GerenteRegistroDTO dto) {
        return ResponseEntity
                .created(URI.create("api/gerente"))
                .body(service.registrarGerente(dto));
    }

    @Operation(
            summary = "Lista todos os gerentes ativos",
            description = "Retorna uma lista de todos os gerentes que não foram logicamente excluídos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de gerentes retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<GerenteResponseDTO>> listarTodosOsGerentes() {
        return ResponseEntity
                .ok(service.listarTodosOsGerentes());
    }

    @Operation(
            summary = "Busca um gerente ativo pelo CPF",
            description = "Retorna dados de um gerente específico com base no CPF.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do gerente", example = "98765432100", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Gerente encontrado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Gerente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"gerente não existente ou inativo(a)!\""))
                    )
            }
    )
    @GetMapping("/{cpf}")
    public ResponseEntity<GerenteResponseDTO> buscarGerente(@PathVariable String cpf) {
        return ResponseEntity
                .ok(service.buscarGerente(cpf));
    }

    @Operation(
            summary = "Atualiza os dados de um gerente",
            description = "Atualiza os dados cadastrais de um gerente existente.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do gerente a ser atualizado", example = "98765432100", required = true)
            },
            requestBody = @RequestBody(
                    description = "Novos dados do gerente",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = GerenteAtualizadoDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nome": "Maria Gerente Silva",
                                      "cpf": "98765432101",
                                      "email": "maria.silva@banco.com",
                                      "senha": "novasenha123"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Gerente atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação (campos inválidos)"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Gerente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"gerente não existente ou inativo(a)!\""))
                    )
            }
    )
    @PutMapping("/{cpf}")
    public ResponseEntity<GerenteResponseDTO> atualizarGerente(@PathVariable String cpf,
                                                               @Valid @org.springframework.web.bind.annotation.RequestBody GerenteAtualizadoDTO dto) {
        return ResponseEntity
                .ok(service.atualizarGerente(cpf, dto));
    }

    @Operation(
            summary = "Desativa (apaga logicamente) um gerente",
            description = "Marca um gerente específico como inativo.",
            parameters = {
                    @Parameter(name = "cpf", description = "CPF do gerente a ser desativado", example = "98765432100", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Gerente desativado com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Gerente não encontrado ou inativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"gerente não existente ou inativo(a)!\""))
                    )
            }
    )
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> apagarGerente(@PathVariable String cpf) {
        service.apagarGerente(cpf);
        return ResponseEntity
                .noContent()
                .build();
    }
}
