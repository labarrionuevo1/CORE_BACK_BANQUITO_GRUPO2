package com.banquito.core.audit.repository;

import com.banquito.core.audit.model.AuditoriaEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {
}
