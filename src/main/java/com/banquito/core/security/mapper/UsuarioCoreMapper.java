package com.banquito.core.security.mapper;

import com.banquito.core.security.dto.UsuarioCoreResponse;
import com.banquito.core.security.model.UsuarioCore;

public class UsuarioCoreMapper {
    
    public static UsuarioCoreResponse toResponse(UsuarioCore usuario) {
        if (usuario == null) {
            return null;
        }
        
        Integer sucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
        
        return new UsuarioCoreResponse(
                usuario.getId(),
                sucursalId,
                usuario.getUsuario(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getUltimoLogin(),
                usuario.getFechaCreacion(),
                usuario.getFechaActualizacion()
        );
    }
}
