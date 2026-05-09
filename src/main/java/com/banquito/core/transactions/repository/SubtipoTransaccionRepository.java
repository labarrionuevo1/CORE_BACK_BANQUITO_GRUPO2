package com.banquito.core.transactions.repository;

import com.banquito.core.transactions.model.SubtipoTransaccion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtipoTransaccionRepository extends JpaRepository<SubtipoTransaccion, Integer> {

    Optional<SubtipoTransaccion> findByCodigo(String codigo);
}
