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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PagamentoAppService {

    private final PagamentoRepository pagamentoRepository;
    private final ContaRepository contaRepository;
    private final TaxaRepository taxaRepository;
    private final PagamentoDomainService domainService;
    //private final MqttService mqttService;

    @PreAuthorize("hasRole('CLIENTE')")
    public PagamentoResponseDTO solicitarPagamento(PagamentoRequestDTO dto) {

        Conta conta = contaRepository.findByNumeroAndAtivaTrue(dto.numeroConta())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta"));

        List<Taxa> taxasLista = taxaRepository.findAll();
        Set<Taxa> taxasAplicaveis = new HashSet<>(taxasLista);

        var valorFinal = domainService.calcularValorFinal(dto.valor(), taxasAplicaveis);

        conta.sacar(valorFinal);
        contaRepository.save(conta);

        Pagamento pagamento = new Pagamento();
        pagamento.setConta(conta);
        pagamento.setBoleto(dto.boleto());
        pagamento.setValorPago(valorFinal);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus(PagamentoStatus.SUCESSO);
        pagamento.setTaxas(taxasAplicaveis);

        pagamentoRepository.save(pagamento);

        return PagamentoResponseDTO.fromEntity(pagamento);

    }
}
