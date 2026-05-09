package com.banquito.core.integration.switchapi.dto.response;

import java.util.UUID;

public record LiquidacionServicioSwitchResponse(
        String estado,
        UUID uuidDebitoMatriz,
        UUID uuidCreditoIngresos,
        UUID uuidCreditoIva,
        UUID uuidGrupoOperacion
) {}
