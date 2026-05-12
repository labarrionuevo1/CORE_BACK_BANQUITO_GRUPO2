package com.banquito.core.parameters.dto.api;

import java.time.LocalDate;

public record DiaHabilResponse(
    LocalDate fechaOriginal,
    LocalDate siguienteDiaHabil,
    Integer diasCalculados,
    String mensaje
) {}
