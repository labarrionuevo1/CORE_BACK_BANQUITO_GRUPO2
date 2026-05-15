package com.banquito.core.security.service;

import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;

public interface UsuarioCoreService {
    
    UsuarioCoreResponse obtenerPorUsername(String username);
    
    boolean validarRolYEstado(String username, RolUsuarioCoreEnum rolRequerido, EstadoUsuarioCoreEnum estadoRequerido);

    UsuarioCoreResponse crear(UsuarioCoreRequest request);
    
}
