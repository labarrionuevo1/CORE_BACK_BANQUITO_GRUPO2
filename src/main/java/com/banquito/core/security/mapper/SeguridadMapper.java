package com.banquito.core.security.mapper;

import com.banquito.core.security.dto.response.CredencialWebResponse;
import com.banquito.core.security.dto.response.UsuarioCoreResponse;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;

public final class SeguridadMapper {
    private SeguridadMapper() {}
    
    public static CredencialWebResponse toResponse(CredencialWeb c) {
        Integer clienteId = c.getCliente() != null ? c.getCliente().getId() : null;
        return new CredencialWebResponse(
                c.getId(), 
                clienteId, 
                c.getUsuario(), 
                c.getEstado(),
                c.getUltimoLogin(),
                c.getFechaCreacion(),
                c.getFechaActualizacion()
        );
    }
    
    public static UsuarioCoreResponse toResponse(UsuarioCore u) {
        Integer sucursalId = u.getSucursal() != null ? u.getSucursal().getId() : null;
        return new UsuarioCoreResponse(
                u.getId(), 
                sucursalId, 
                u.getUsuario(), 
                u.getNombreCompleto(), 
                u.getRol(), 
                u.getEstado(),
                u.getUltimoLogin(),
                u.getFechaCreacion(),
                u.getFechaActualizacion()
        );
    }
}
