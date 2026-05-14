package com.banquito.core.security.dto.api;

import java.time.LocalDateTime;

public record LoginResponse(
        String tipoUsuario,
        Integer usuarioCoreId,
        Integer credencialWebId,
        Integer clienteId,
        String usuario,
        String nombre,
        String rol,
        String estado,
        LocalDateTime ultimoLogin
) {
}
