package com.banquito.core.parameters.dto.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FeriadoRequest(
    @NotNull(message = "La fecha del feriado es obligatoria")
    @FutureOrPresent(message = "La fecha del feriado no puede ser en el pasado")
    LocalDate fecha,

    @NotBlank(message = "El nombre del feriado es obligatorio")
    String nombre,

    String estado
) {}
