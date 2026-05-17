package com.banquito.core.integration.switchapi.dto.api;

public record LoginResponse(
        Boolean autenticado,
        String tipoUsuario,
        String credencialWebId,
        String clienteId,
        String rucEmpresa,
        String usuario,
        String nombre,
        String rolSwitch,
        String estado,
        Boolean activoPagosMasivos
) {}
