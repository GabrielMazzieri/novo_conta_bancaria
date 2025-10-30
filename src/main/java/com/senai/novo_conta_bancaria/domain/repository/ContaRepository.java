package com.senai.novo_conta_bancaria.domain.repository;

import com.senai.novo_conta_bancaria.domain.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, String> {

    List<Conta> findAllByAtivaTrue();
    Optional<Conta> findByNumeroAndAtivaTrue(String numero);

}
