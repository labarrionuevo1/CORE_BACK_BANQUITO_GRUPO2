package com.banquito.core.audit.repository;

import com.banquito.core.audit.model.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {

    List<AuditoriaEvento> findByEntidadAndEntidadIdOrderByFechaEventoDesc(
            String entidad,
            String entidadId
    );

    List<AuditoriaEvento> findByModuloOrderByFechaEventoDesc(String modulo);
}