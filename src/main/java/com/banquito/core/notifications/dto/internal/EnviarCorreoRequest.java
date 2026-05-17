package com.banquito.core.notifications.dto.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnviarCorreoRequest(

        @Email(message = "El destinatario debe ser un correo valido")
        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        @NotBlank(message = "El asunto es obligatorio")
        String asunto,

        @NotBlank(message = "El contenido es obligatorio")
        String contenido
) {
}
