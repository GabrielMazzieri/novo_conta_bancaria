package com.senai.novo_conta_bancaria.domain.repository;

import com.senai.novo_conta_bancaria.domain.entity.DispositivoIoT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, String> {
    Optional<DispositivoIoT> findByClienteIdAndAtivoTrue(String clienteId);
    Optional<DispositivoIoT> findByCodigoSerial(String codigoSerial);
}
