package com.banquito.core.parameters.dto.api;

import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import java.time.LocalDate;

public record FeriadoResponse(
    LocalDate fechaFeriado, 
    String nombre, 
    Boolean esFinSemana, 
    EstadoCatalogoEnum estado
) {}
