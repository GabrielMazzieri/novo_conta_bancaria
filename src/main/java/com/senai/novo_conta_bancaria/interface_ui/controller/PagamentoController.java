package com.senai.novo_conta_bancaria.interface_ui.controller;

import com.senai.novo_conta_bancaria.application.dto.PagamentoRequestDTO;
import com.senai.novo_conta_bancaria.application.dto.PagamentoResponseDTO;
import com.senai.novo_conta_bancaria.application.service.PagamentoAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Operações de pagamentos de boletos")
public class PagamentoController {

    private final PagamentoAppService service;

    @Operation(summary = "Realizar pagamento de boleto", description = "Calcula taxas, valida saldo e efetua o pagamento.")
    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> realizarPagamento(@RequestBody @Valid PagamentoRequestDTO dto){
        return ResponseEntity.ok(service.solicitarPagamento(dto));
    }
}
