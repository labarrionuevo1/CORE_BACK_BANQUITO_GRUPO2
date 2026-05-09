package com.banquito.core.parameters.repository;

import com.banquito.core.parameters.model.Feriado;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeriadoRepository extends JpaRepository<Feriado, LocalDate> {
}
