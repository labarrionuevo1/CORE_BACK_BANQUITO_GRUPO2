package com.banquito.core.institutional.repository;

import com.banquito.core.institutional.model.CuentaInstitucional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaInstitucionalRepository extends JpaRepository<CuentaInstitucional, Integer> {

    Optional<CuentaInstitucional> findByCodigo(String codigo);
    Optional<CuentaInstitucional> findByNumeroCuenta(String numeroCuenta);
}
