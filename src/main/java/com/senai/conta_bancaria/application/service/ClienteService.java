package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ClienteAtualizadoDTO;
import com.senai.conta_bancaria.application.dto.ClienteRegistroDTO;
import com.senai.conta_bancaria.application.dto.ClienteResponseDTO;
import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.exception.ContaMesmoTipoException;
import com.senai.conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.conta_bancaria.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteResponseDTO registrarCliente(ClienteRegistroDTO dto) {

        var cliente = repository.findByCpfAndAtivoTrue(dto.cpf()).orElseGet(
                () -> repository.save(dto.toEntity())
                );
        var contas = cliente.getContas();
        var novaConta = dto.contaDTO().toEntity(cliente);

        boolean jaTemTipo = contas.stream().anyMatch(
                c -> c.getClass().equals(novaConta.getClass()) && c.isAtiva()
                );
        if (jaTemTipo)
            throw new ContaMesmoTipoException();

        cliente.getContas().add(novaConta);

        return ClienteResponseDTO.fromEntity(repository.save(cliente));
    }

    public List<ClienteResponseDTO> listarClientesAtivos() {
        return repository.findAllByAtivoTrue().stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public ClienteResponseDTO buscarClienteAtivoPorCpf(String cpf) {
        var cliente = buscarClientePorCpfEAtivo(cpf);
        return ClienteResponseDTO.fromEntity(cliente);
    }



    public ClienteResponseDTO atualizarCliente(String cpf, ClienteAtualizadoDTO dto) {
        var cliente = buscarClientePorCpfEAtivo(cpf);

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());

        return ClienteResponseDTO.fromEntity(repository.save(cliente));
    }

    public void deletarCliente(String cpf) {
        var cliente = buscarClientePorCpfEAtivo(cpf);
        cliente.setAtivo(false);

        cliente.getContas().forEach(
                conta -> conta.setAtiva(false)
        );
        repository.save(cliente);
    }

    private Cliente buscarClientePorCpfEAtivo(String cpf) {
        var cliente = repository.findByCpfAndAtivoTrue(cpf).orElseThrow(
                () -> new EntidadeNaoEncontradaException("cliente")
        );
        return cliente;
    }
}
