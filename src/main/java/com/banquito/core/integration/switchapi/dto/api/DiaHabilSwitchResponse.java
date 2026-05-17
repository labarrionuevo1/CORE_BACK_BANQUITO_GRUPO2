package com.banquito.core.integration.switchapi.dto.api;

import java.time.LocalDate;

public record DiaHabilSwitchResponse(
        LocalDate fecha,
        Boolean esDiaHabil,
        Boolean esFinSemana,
        Boolean esFeriado,
        LocalDate siguienteDiaHabil,
        String codigo,
        String mensaje
) {
}