package com.banquito.core.security.dto.api;

import com.banquito.core.security.enums.EstadoCredencialWebEnum;

import java.time.LocalDateTime;

public record CredencialWebResponse(
        Integer id, 
        Integer clienteId, 
        String usuario, 
        EstadoCredencialWebEnum estado,
        LocalDateTime ultimoLogin,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {}
