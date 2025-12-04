package com.senai.novo_conta_bancaria.domain.repository;

import com.senai.novo_conta_bancaria.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    Optional<Cliente> findByCpfAndAtivoTrue(String cpf);
    Boolean existsByEmailAndAtivoTrue(String email);

    List<Cliente> findAllByAtivoTrue();
}
