package com.senai.novo_conta_bancaria.application.service;

import com.senai.novo_conta_bancaria.application.dto.PagamentoRequestDTO;
import com.senai.novo_conta_bancaria.application.dto.PagamentoResponseDTO;
import com.senai.novo_conta_bancaria.domain.entity.Conta;
import com.senai.novo_conta_bancaria.domain.entity.Pagamento;
import com.senai.novo_conta_bancaria.domain.entity.Taxa;
import com.senai.novo_conta_bancaria.domain.enums.PagamentoStatus;
import com.senai.novo_conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.novo_conta_bancaria.domain.repository.ContaRepository;
import com.senai.novo_conta_bancaria.domain.repository.PagamentoRepository;
import com.senai.novo_conta_bancaria.domain.repository.TaxaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class PagamentoAppService {

    private final PagamentoRepository pagamentoRepository;
    private final ContaRepository contaRepository;
    private final TaxaRepository taxaRepository;
    private final PagamentoDomainService domainService;
    private final AutenticacaoIoTService autenticacaoIoTService;

    @Autowired
    @Lazy
    private PagamentoAppService self;

    @PreAuthorize("hasRole('CLIENTE')")
    public PagamentoResponseDTO solicitarPagamento(PagamentoRequestDTO dto) {
        domainService.validarVencimento(dto.dataVencimento());

        Conta conta = contaRepository.findByNumeroAndAtivaTrue(dto.numeroConta())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta não encontrada"));

        autenticacaoIoTService.solicitarAutenticacaoBiometrica(conta.getCliente());

        List<Taxa> listaTaxas = taxaRepository.findByTipoPagamento(dto.formaPagamento());
        Set<Taxa> taxasAplicaveis = new HashSet<>(listaTaxas);

        var valorFinal = domainService.calcularValorFinal(dto.valor(), taxasAplicaveis);
        domainService.validarSaldoSuficiente(conta, valorFinal);

        return self.concluirPagamento(conta, dto, valorFinal, taxasAplicaveis);
    }

    @Transactional
    public PagamentoResponseDTO concluirPagamento(Conta conta, PagamentoRequestDTO dto, java.math.BigDecimal valorFinal, Set<Taxa> taxasAplicaveis) {

        conta.sacar(valorFinal);
        contaRepository.save(conta);

        Pagamento pagamento = new Pagamento();
        pagamento.setConta(conta);
        pagamento.setBoleto(dto.boleto());
        pagamento.setValorPago(valorFinal);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus(PagamentoStatus.SUCESSO);
        pagamento.setFormaPagamento(dto.formaPagamento());
        pagamento.setTaxas(taxasAplicaveis);

        pagamentoRepository.save(pagamento);

        return PagamentoResponseDTO.fromEntity(pagamento);
    }
}