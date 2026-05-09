package com.banquito.core.integration.switchapi.dto.response;

public record ValidarEmpresaSwitchResponse(
        String ruc,
        Boolean existe,
        String estado,
        Boolean activoPagosMasivos
) {}
