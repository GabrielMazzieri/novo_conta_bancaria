package com.senai.novo_conta_bancaria.application.service;

import com.senai.novo_conta_bancaria.application.dto.GerenteAtualizadoDTO;
import com.senai.novo_conta_bancaria.application.dto.GerenteRegistroDTO;
import com.senai.novo_conta_bancaria.application.dto.GerenteResponseDTO;
import com.senai.novo_conta_bancaria.application.dto.GerenteResponseDTO;
import com.senai.novo_conta_bancaria.domain.entity.Gerente;
import com.senai.novo_conta_bancaria.domain.exception.EmailExistenteException;
import com.senai.novo_conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.novo_conta_bancaria.domain.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GerenteService {
    private final GerenteRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public GerenteResponseDTO registrarGerente(GerenteRegistroDTO dto) {
        repository.findByEmail(dto.email()).ifPresent(gerente -> {
            if (gerente.isAtivo()) {
                throw new EmailExistenteException("Endereço de e-mail \"" + dto.email() + "\" já foi cadastrado.");
            }
        });

        Gerente gerenteRegistrado = repository
                .findByCpfAndAtivoTrue(dto.cpf())
                .orElseGet(
                        () -> repository.save(dto.toEntity())
                );

        gerenteRegistrado.setSenha(passwordEncoder.encode(dto.senha()));
        return GerenteResponseDTO.fromEntity(repository.save(gerenteRegistrado));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<GerenteResponseDTO> listarTodosOsGerentes() {
        return repository
                .findAllByAtivoTrue()
                .stream()
                .map(GerenteResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public GerenteResponseDTO buscarGerente(String cpf) {
        return GerenteResponseDTO.fromEntity(procurarGerenteAtivo(cpf));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GerenteResponseDTO atualizarGerente(String cpf, GerenteAtualizadoDTO dto) {
        Gerente gerente = procurarGerenteAtivo(cpf);

        gerente.setNome(dto.nome());
        gerente.setCpf(dto.cpf());
        gerente.setEmail(dto.email());
        gerente.setSenha(dto.senha());

        return GerenteResponseDTO.fromEntity(repository.save(gerente));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void apagarGerente(String cpf) {
        Gerente gerente = procurarGerenteAtivo(cpf);

        gerente.setAtivo(false);

        repository.save(gerente);
    }

    private Gerente procurarGerenteAtivo(String cpf) {
        return repository
                .findByCpfAndAtivoTrue(cpf)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("gerente"));
    }
}

