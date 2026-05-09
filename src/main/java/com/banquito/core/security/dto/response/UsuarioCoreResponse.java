package com.banquito.core.security.dto.response;

import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;

public record UsuarioCoreResponse(Integer id, Integer sucursalId, String usuario, String nombreCompleto, RolUsuarioCoreEnum rol, EstadoUsuarioCoreEnum estado) {}
