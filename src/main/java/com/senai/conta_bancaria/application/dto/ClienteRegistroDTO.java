package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;

import java.util.ArrayList;
import java.util.List;

public record ClienteRegistroDTO(
    String nome,
    String cpf,
    ContaResumoDTO conta
){

    public Cliente toEntity(){
        return Cliente.builder()
                .status(true)
                .nome(this.nome)
                .CPF(this.cpf)
                .contas(new ArrayList<Conta>())
                .build();
    }
}
