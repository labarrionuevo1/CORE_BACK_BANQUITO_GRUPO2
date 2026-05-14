package com.banquito.core.integration.switchapi.dto.api;

public record ValidarEmpresaSwitchResponse(
        String ruc,
        Boolean existe,
        String estado,
        Boolean activoPagosMasivos
) {}
