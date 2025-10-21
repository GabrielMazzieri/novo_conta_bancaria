package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.GerenteAtualizadoDTO;
import com.senai.conta_bancaria.application.dto.GerenteRegistroDTO;
import com.senai.conta_bancaria.application.dto.GerenteResponseDTO;
import com.senai.conta_bancaria.application.service.GerenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/gerente")
@RequiredArgsConstructor
public class GerenteController {
    private final GerenteService service;

    @PostMapping
    public ResponseEntity<GerenteResponseDTO> registrarGerente(@Valid @RequestBody GerenteRegistroDTO dto) {
        return ResponseEntity
                .created(URI.create("api/gerente"))
                .body(service.registrarGerente(dto));
    }

    @GetMapping
    public ResponseEntity<List<GerenteResponseDTO>> listarTodosOsGerentes() {
        return ResponseEntity
                .ok(service.listarTodosOsGerentes());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<GerenteResponseDTO> buscarGerente(@PathVariable String cpf) {
        return ResponseEntity
                .ok(service.buscarGerente(cpf));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<GerenteResponseDTO> atualizarGerente(@PathVariable String cpf,
                                                               @Valid @RequestBody GerenteAtualizadoDTO dto) {
        return ResponseEntity
                .ok(service.atualizarGerente(cpf, dto));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> apagarGerente(@PathVariable String cpf) {
        service.apagarGerente(cpf);
        return ResponseEntity
                .noContent()
                .build();
    }
}