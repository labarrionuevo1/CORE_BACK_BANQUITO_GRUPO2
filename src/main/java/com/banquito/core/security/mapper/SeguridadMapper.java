package com.banquito.core.security.mapper;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;

public final class SeguridadMapper {

    private SeguridadMapper() {
    }

    public static CredencialWebResponse toResponse(CredencialWeb credencial) {
        if (credencial == null) {
            return null;
        }

        Integer clienteId = credencial.getCliente() != null
                ? credencial.getCliente().getId()
                : null;

        return new CredencialWebResponse(
                credencial.getId(),
                clienteId,
                credencial.getUsuario(),
                credencial.getEstado(),
                credencial.getUltimoLogin(),
                credencial.getFechaCreacion(),
                credencial.getFechaActualizacion()
        );
    }

    public static UsuarioCoreResponse toResponse(UsuarioCore usuario) {
        if (usuario == null) {
            return null;
        }

        Integer sucursalId = usuario.getSucursal() != null
                ? usuario.getSucursal().getId()
                : null;

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

    public static UsuarioCore toEntity(UsuarioCoreRequest request) {
        if (request == null) {
            return null;
        }

        UsuarioCore usuario = new UsuarioCore();
        usuario.setUsuario(request.usuario());
        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setRol(RolUsuarioCoreEnum.valueOf(request.rol()));
        return usuario;
    }
}
