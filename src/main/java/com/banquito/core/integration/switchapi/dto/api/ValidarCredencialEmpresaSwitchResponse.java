package com.banquito.core.integration.switchapi.dto.api;

import com.banquito.core.security.enums.EstadoCredencialWebEnum;

public record ValidarCredencialEmpresaSwitchResponse(
        String ruc,
        String username,
        Boolean existe,
        Boolean perteneceEmpresa,
        EstadoCredencialWebEnum estado,
        Boolean valida,
        String codigo,
        String mensaje
) {
}