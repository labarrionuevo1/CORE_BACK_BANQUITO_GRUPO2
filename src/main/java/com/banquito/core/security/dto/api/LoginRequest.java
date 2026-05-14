package com.banquito.core.security.dto.api;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String usuario,

        @NotBlank(message = "La contrasena es obligatoria")
        String contrasena
) {
}
