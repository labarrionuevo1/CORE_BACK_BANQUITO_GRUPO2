package com.banquito.core.security.dto.api;

import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import jakarta.validation.constraints.NotNull;

public record UsuarioCoreEstadoRequest(
    @NotNull(message = "El estado es obligatorio")
    EstadoUsuarioCoreEnum estado
) {}
