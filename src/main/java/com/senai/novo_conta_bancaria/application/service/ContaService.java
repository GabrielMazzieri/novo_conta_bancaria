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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaService {
    private final ContaRepository contaRepository;
    private final AutenticacaoIoTService autenticacaoIoTService;

    @Autowired
    @Lazy
    private ContaService self;

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Transactional(readOnly = true)
    public List<ContaResumoDTO> listarTodasContas() {
        return contaRepository.findAllByAtivaTrue().stream()
                .map(ContaResumoDTO::fromEntity).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CLIENTE')")
    @Transactional(readOnly = true)
    public ContaResumoDTO buscarContaPorNumero(String numero) {
        Conta conta = buscarContaAtivaPorNumero(numero);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var emailUsuarioLogado = authentication.getName();
        var roleUsuario = authentication.getAuthorities().stream()
                .findFirst().get().getAuthority();

        if ("ROLE_CLIENTE".equals(roleUsuario)) {
            if (!conta.getCliente().getEmail().equals(emailUsuarioLogado)) {
                throw new AccessDeniedException("Acesso negado: Você só pode acessar sua própria conta.");
            }
        }

        return ContaResumoDTO.fromEntity(conta);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Transactional
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
        return self.efetivarSaque(conta, dto.valor());
    }

    @Transactional
    public ContaResumoDTO efetivarSaque(Conta conta, BigDecimal valor) {
        conta.sacar(valor);
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

        autenticacaoIoTService.solicitarAutenticacaoBiometrica(contaOrigem.getCliente());
        return self.efetivarTransferencia(contaOrigem, contaDestino, dto.valor());
    }

    @Transactional
    public ContaResumoDTO efetivarTransferencia(Conta origem, Conta destino, BigDecimal valor) {
        origem.transferir(valor, destino);
        contaRepository.save(destino);
        return ContaResumoDTO.fromEntity(contaRepository.save(origem));
    }

    private Conta buscarContaAtivaPorNumero(String numero) {
        return contaRepository.findByNumeroAndAtivaTrue(numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("conta"));
    }

    @Transactional
    public ContaResumoDTO aplicarRendimento(String numeroDaConta) {
        Conta conta = buscarContaAtivaPorNumero(numeroDaConta);
        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.aplicarRendimento();
            return ContaResumoDTO.fromEntity(contaRepository.save(poupanca));
        }
        throw new RendimentoInvalidoException();
    }
}
