package com.banquito.core.security.dto;

import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;

import java.time.LocalDateTime;

public record UsuarioCoreResponse(
        Integer id, 
        Integer sucursalId, 
        String usuario, 
        String nombreCompleto, 
        RolUsuarioCoreEnum rol, 
        EstadoUsuarioCoreEnum estado,
        LocalDateTime ultimoLogin,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {}
