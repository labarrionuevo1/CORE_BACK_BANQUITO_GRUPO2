package com.banquito.core.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banquito.core.accounts.model.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    List<Cuenta> findByClienteId(Integer clienteId);
    Optional<Cuenta> findFirstByClienteIdAndEsFavoritaPagosTrue(Integer clienteId);
}
