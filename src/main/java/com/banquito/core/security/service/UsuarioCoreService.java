package com.banquito.core.security.service;

import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;

import java.util.List;

public interface UsuarioCoreService {
    
    List<UsuarioCoreResponse> listar();

    UsuarioCoreResponse obtener(Integer id);

    UsuarioCoreResponse obtenerPorUsername(String username);
    
    boolean validarRolYEstado(String username, RolUsuarioCoreEnum rolRequerido, EstadoUsuarioCoreEnum estadoRequerido);

    UsuarioCoreResponse crear(UsuarioCoreRequest request);

    UsuarioCoreResponse actualizar(Integer id, UsuarioCoreRequest request);

    UsuarioCoreResponse cambiarEstado(Integer id, EstadoUsuarioCoreEnum nuevoEstado);
    
}
