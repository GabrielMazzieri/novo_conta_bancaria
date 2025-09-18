package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ClienteRegistroDTO;
import com.senai.conta_bancaria.application.dto.ClienteResponseDTO;
import com.senai.conta_bancaria.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ClienteService {

    private final ClienteRepository repository;

    public ClienteResponseDTO registrarCliente(ClienteRegistroDTO dto){

        var cliente = repository.findByCpfAndAtivoTrue(dto.cpf()).orElseGet(
                () ->  repository.save(dto.toEntity())
        );
        var contas = cliente.getContas();
        var novaConta = dto.conta().toEntity(cliente);

        boolean jaTemTipo = contas.stream()
                .anyMatch(Conta c -> c.getClass().equals(novaConta.getClass()) && c.isStatus().isTrue());

        return
    }
}
