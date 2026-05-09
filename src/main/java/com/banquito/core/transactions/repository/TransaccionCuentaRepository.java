package com.banquito.core.transactions.repository;

import com.banquito.core.transactions.model.TransaccionCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionCuentaRepository extends JpaRepository<TransaccionCuenta, Long> {
}
