package com.banquito.core.security.mapper;

import com.banquito.core.security.dto.response.CredencialWebResponse;
import com.banquito.core.security.dto.response.UsuarioCoreResponse;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;

public final class SeguridadMapper {
    private SeguridadMapper() {}
    public static CredencialWebResponse toResponse(CredencialWeb c) {
        return new CredencialWebResponse(c.getId(), c.getCliente().getId(), c.getUsuario(), c.getEstado());
    }
    public static UsuarioCoreResponse toResponse(UsuarioCore u) {
        Integer sucursalId = u.getSucursal() == null ? null : u.getSucursal().getId();
        return new UsuarioCoreResponse(u.getId(), sucursalId, u.getUsuario(), u.getNombreCompleto(), u.getRol(), u.getEstado());
    }
}
