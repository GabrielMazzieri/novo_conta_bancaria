package com.senai.novo_conta_bancaria.application.service;

import com.senai.novo_conta_bancaria.application.dto.ContaAtualizacaoDTO;
import com.senai.novo_conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.novo_conta_bancaria.application.dto.TransferenciaDTO;
import com.senai.novo_conta_bancaria.application.dto.ValorSaqueDepositoDTO;
import com.senai.novo_conta_bancaria.domain.entity.Conta;
import com.senai.novo_conta_bancaria.domain.entity.ContaCorrente;
import com.senai.novo_conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.novo_conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.novo_conta_bancaria.domain.exception.RendimentoInvalidoException;
import com.senai.novo_conta_bancaria.domain.exception.TipoDeContaInvalidaException;
import com.senai.novo_conta_bancaria.domain.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {
    private final ContaRepository contaRepository;
    private final AutenticacaoIoTService autenticacaoIoTService;

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Transactional(readOnly = true)
    public List<ContaResumoDTO> listarTodasContas() {
        return contaRepository.findAllByAtivaTrue().stream()
                .map(ContaResumoDTO::fromEntity).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Transactional(readOnly = true)
    public ContaResumoDTO buscarContaPorNumero(String numero) {
        return ContaResumoDTO.fromEntity(
                buscarContaAtivaPorNumero(numero)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ContaResumoDTO atualizarConta(String numeroDaConta, ContaAtualizacaoDTO dto) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);

        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.setRendimento(dto.rendimento());
        } else if (conta instanceof ContaCorrente corrente) {
            corrente.setLimite(dto.limite());
            corrente.setTaxa(dto.taxa());
        } else {
            throw new TipoDeContaInvalidaException("");
        }
        conta.setSaldo(dto.saldo());

        return ContaResumoDTO.fromEntity(contaRepository.save(conta));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public void deletarConta(String numeroDaConta) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);
        conta.setAtiva(false);
        contaRepository.save(conta);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    public ContaResumoDTO sacar(String numeroDaConta, ValorSaqueDepositoDTO dto) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);

        autenticacaoIoTService.solicitarAutenticacaoBiometrica(conta.getCliente());
        conta.sacar(dto.valor());
        return ContaResumoDTO.fromEntity(contaRepository.save(conta));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    public ContaResumoDTO depositar(String numeroDaConta, ValorSaqueDepositoDTO dto) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);

        conta.depositar(dto.valor());
        return ContaResumoDTO.fromEntity(contaRepository.save(conta));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    public ContaResumoDTO transferir(String numeroDaConta, TransferenciaDTO dto) {
        Conta contaOrigem = buscarContaAtivaPorNumero(numeroDaConta);
        Conta contaDestino = buscarContaAtivaPorNumero(dto.contaDestino());

        contaOrigem.transferir(dto.valor(), contaDestino);

        contaRepository.save(contaDestino);
        return ContaResumoDTO.fromEntity(contaRepository.save(contaOrigem));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    private Conta buscarContaAtivaPorNumero(String numero) {
        return contaRepository.findByNumeroAndAtivaTrue(numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("conta"));
    }

    public ContaResumoDTO aplicarRendimento(String numeroDaConta) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);
        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.aplicarRendimento();
            return ContaResumoDTO.fromEntity(contaRepository.save(poupanca));
        }
        throw new RendimentoInvalidoException();
    }
}
