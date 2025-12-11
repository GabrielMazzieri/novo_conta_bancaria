package com.senai.novo_conta_bancaria.interface_ui.controller;

import com.senai.novo_conta_bancaria.application.dto.TaxaRequestDTO;
import com.senai.novo_conta_bancaria.application.dto.TaxaResponseDTO;
import com.senai.novo_conta_bancaria.application.service.TaxaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/taxas")
@RequiredArgsConstructor
@Tag(name = "Taxas", description = "Operações relacionadas a taxas bancárias")
public class TaxaController {

    private final TaxaService service;

    @Operation(summary = "Cadastrar nova taxa", description = "Apenas para gerentes podem realizar.")
    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<TaxaResponseDTO> criarTaxa(@RequestBody @Valid TaxaRequestDTO dto) {
        TaxaResponseDTO novaTaxa = service.criarTaxa(dto);
        return ResponseEntity.created(URI.create("/api/taxas/" + novaTaxa.id()))
                .body(novaTaxa);
    }

    @Operation(summary = "Lista todas as taxas")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaxaResponseDTO>> listarTaxas() {
        return ResponseEntity.ok(service.listarTaxas());
    }

    @Operation(summary = "Busca uma taxa por ID")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaxaResponseDTO> buscarTaxa(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarTaxaPorId(id));
    }

    @Operation(summary = "Deleta uma taxa")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deletarTaxa(@PathVariable String id) {
        service.deletarTaxa(id);
        return ResponseEntity.noContent().build();
    }
}
