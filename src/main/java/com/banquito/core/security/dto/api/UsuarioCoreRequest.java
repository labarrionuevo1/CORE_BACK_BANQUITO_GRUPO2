package com.banquito.core.security.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCoreRequest(
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede tener más de 50 caracteres")
    String usuario,

    @NotBlank(message = "La contraseña es obligatoria")
    String contrasena,

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre completo no puede tener más de 150 caracteres")
    String nombreCompleto,

    @NotBlank(message = "El rol es obligatorio")
    String rol,

    Integer sucursalId
) {}
