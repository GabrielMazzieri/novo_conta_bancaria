package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.ContaAtualizacaoDTO;
import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.application.dto.TransferenciaDTO;
import com.senai.conta_bancaria.application.dto.ValorSaqueDepositoDTO;
import com.senai.conta_bancaria.application.service.ContaService;
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

import java.util.List;

@Tag(name = "Contas", description = "Gerenciamento de Contas da Conta Bancaria")
@RestController
@RequestMapping("/api/conta")
@RequiredArgsConstructor
public class ContaController {
    private final ContaService service;

    @Operation(
            summary = "Lista todas as contas ativas",
            description = "Retorna uma lista de todas as contas ativas no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso")
            }
    )
    @GetMapping
    public ResponseEntity<List<ContaResumoDTO>> listarTodasContas() {
        return ResponseEntity.ok(service.listarTodasContas());
    }

    @Operation(
            summary = "Busca uma conta ativa pelo número",
            description = "Retorna os dados de uma conta específica com base no seu número.",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta", example = "12345-6", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Conta não encontrada ou inativa",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"conta não existente ou inativa(a)!\""))
                    )
            }
    )
    @GetMapping("/{numeroDaConta}")
    public ResponseEntity<ContaResumoDTO> buscarContaPorNumero(@PathVariable String numeroDaConta) {
        return ResponseEntity.ok(service.buscarContaPorNumero(numeroDaConta));
    }

    @Operation(
            summary = "Atualiza dados de uma conta",
            description = "Atualiza saldo, limite (para Conta Corrente), taxa (para CC) ou rendimento (para Conta Poupança).",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta a ser atualizada", example = "12345-6", required = true)
            },
            requestBody = @RequestBody(
                    description = "Dados da conta a serem atualizados",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ContaAtualizacaoDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "saldo": 1500.00,
                                      "limite": 500.00,
                                      "rendimento": 0.01,
                                      "taxa": 0.05
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou tipo de conta inválido"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada ou inativa")
            }
    )
    @PutMapping("/{numeroDaConta}")
    public ResponseEntity<ContaResumoDTO> atualizarConta(@PathVariable String numeroDaConta,
                                                         @Valid @org.springframework.web.bind.annotation.RequestBody ContaAtualizacaoDTO dto) {
        return ResponseEntity.ok(service.atualizarConta(numeroDaConta, dto));
    }

    @Operation(
            summary = "Desativa (apaga logicamente) uma conta",
            description = "Marca uma conta específica como inativa.",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta a ser desativada", example = "12345-6", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Conta desativada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada ou inativa")
            }
    )
    @DeleteMapping("/{numeroDaConta}")
    public ResponseEntity<Void> deletarConta(@PathVariable String numeroDaConta) {
        service.deletarConta(numeroDaConta);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Realiza um saque em uma conta",
            description = "Subtrai um valor do saldo da conta. Para Conta Corrente, considera a taxa e o limite.",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta", example = "12345-6", required = true)
            },
            requestBody = @RequestBody(
                    description = "Valor a ser sacado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ValorSaqueDepositoDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "valor": 100.00
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Saldo insuficiente ou valor negativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = {
                                            @ExampleObject(name = "Saldo Insuficiente", value = "\"Saldo insuficiente para realizar a operação de saque\""),
                                            @ExampleObject(name = "Valor Negativo", value = "\"Não é possível realizar saque com valores negativos.\"")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada ou inativa")
            }
    )
    @PostMapping("/{numeroDaConta}/sacar")
    public ResponseEntity<ContaResumoDTO> sacar(@PathVariable String numeroDaConta,
                                                @Valid @org.springframework.web.bind.annotation.RequestBody ValorSaqueDepositoDTO dto) {
        return ResponseEntity.ok(service.sacar(numeroDaConta, dto));
    }

    @Operation(
            summary = "Realiza um depósito em uma conta",
            description = "Adiciona um valor ao saldo da conta.",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta", example = "12345-6", required = true)
            },
            requestBody = @RequestBody(
                    description = "Valor a ser depositado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ValorSaqueDepositoDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "valor": 250.00
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Valor negativo",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(name = "Valor Negativo", value = "\"Não é possível realizar depósito com valores negativos.\""))
                    ),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada ou inativa")
            }
    )
    @PostMapping("/{numeroDaConta}/depositar")
    public ResponseEntity<ContaResumoDTO> depositar(@PathVariable String numeroDaConta,
                                                    @Valid @org.springframework.web.bind.annotation.RequestBody ValorSaqueDepositoDTO dto) {
        return ResponseEntity.ok(service.depositar(numeroDaConta, dto));
    }

    @Operation(
            summary = "Realiza uma transferência entre contas",
            description = "Transfere um valor de uma conta de origem (no path) para uma conta de destino (no body).",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta de *origem*", example = "12345-6", required = true)
            },
            requestBody = @RequestBody(
                    description = "Conta de destino e valor",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransferenciaDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "contaDestino": "98765-4",
                                      "valor": 150.00
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Saldo insuficiente ou transferência para a mesma conta",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = {
                                            @ExampleObject(name = "Saldo Insuficiente", value = "\"Saldo insuficiente para realizar a operação de saque\""),
                                            @ExampleObject(name = "Mesma Conta", value = "\"Não é possível transferir para a mesma conta.\"")
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Conta de origem ou destino não encontrada")
            }
    )
    @PostMapping("/{numeroDaConta}/transferir")
    public ResponseEntity<ContaResumoDTO> transferir(@PathVariable String numeroDaConta,
                                                     @Valid @org.springframework.web.bind.annotation.RequestBody TransferenciaDTO dto) {
        return ResponseEntity.ok(service.transferir(numeroDaConta, dto));
    }

    @Operation(
            summary = "Aplica o rendimento em uma conta poupança",
            description = "Calcula e adiciona o rendimento ao saldo de uma conta poupança. Falha se a conta não for poupança.",
            parameters = {
                    @Parameter(name = "numeroDaConta", description = "Número da conta poupança", example = "98765-4", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rendimento aplicado com sucesso"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Conta não é poupança",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class),
                                    examples = @ExampleObject(value = "\"Rendimento deve ser aplicado somente em conta poupança!\""))
                    ),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada ou inativa")
            }
    )
    @PostMapping("/{numeroDaConta}/rendimento")
    public ResponseEntity<ContaResumoDTO> aplicarRendimento(@PathVariable String numeroDaConta) {
        return ResponseEntity.ok(service.aplicarRendimento(numeroDaConta));
    }

}
