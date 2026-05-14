package com.banquito.core.security.mapper;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.model.CredencialWeb;

public class CredencialWebMapper {
    
    public static CredencialWebResponse toResponse(CredencialWeb credencial) {
        if (credencial == null) {
            return null;
        }
        
        Integer clienteId = credencial.getCliente() != null ? credencial.getCliente().getId() : null;
        
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
}
