package com.banquito.core.security.service;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;

public interface CredencialWebService {
    
    CredencialWebResponse obtenerPorUsername(String username);
    
    boolean validarEstado(String username, EstadoCredencialWebEnum estadoRequerido);
    
}
