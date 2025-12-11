package com.senai.novo_conta_bancaria.application.service;

import com.senai.novo_conta_bancaria.application.dto.TaxaRequestDTO;
import com.senai.novo_conta_bancaria.application.dto.TaxaResponseDTO;
import com.senai.novo_conta_bancaria.domain.entity.Taxa;
import com.senai.novo_conta_bancaria.domain.exception.EntidadeNaoEncontradaException;
import com.senai.novo_conta_bancaria.domain.repository.TaxaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaxaService {

    private final TaxaRepository repository;

    @PreAuthorize("hasRole('GERENTE')")
    public TaxaResponseDTO criarTaxa(TaxaRequestDTO dto){
        Taxa taxa = new Taxa();
        taxa.setDescricao(dto.descricao());
        taxa.setPercentual(dto.percentual());
        taxa.setValorFixo(dto.valorFixo() != null ? dto.valorFixo() : BigDecimal.ZERO);

        taxa.setTipoPagamento(dto.tipoPagamento());

        return TaxaResponseDTO.fromEntity(repository.save(taxa));
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    @Transactional(readOnly = true)
    public List<TaxaResponseDTO> listarTaxas(){
        return repository.findAll()
                .stream()
                .map(TaxaResponseDTO::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('GERENTE')")
    public TaxaResponseDTO buscarTaxaPorId(String id){
        return TaxaResponseDTO.fromEntity(buscarTaxa(id));
    }

    @PreAuthorize("hasRole('GERENTE')")
    public void deletarTaxa(String id){
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Taxa");
        }
        repository.deleteById(id);
    }

    private Taxa buscarTaxa(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Taxa"));
    }
}

