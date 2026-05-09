package com.banquito.core.accounts.repository;

import com.banquito.core.accounts.model.HistorialEstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialEstadoCuentaRepository extends JpaRepository<HistorialEstadoCuenta, Long> {
}
