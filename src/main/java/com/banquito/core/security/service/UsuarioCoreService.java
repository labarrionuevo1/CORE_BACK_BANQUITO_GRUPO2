package com.banquito.core.security.service;

import com.banquito.core.security.dto.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;

public interface UsuarioCoreService {
    
    UsuarioCoreResponse obtenerPorUsername(String username);
    
    boolean validarRolYEstado(String username, RolUsuarioCoreEnum rolRequerido, EstadoUsuarioCoreEnum estadoRequerido);
    
}
