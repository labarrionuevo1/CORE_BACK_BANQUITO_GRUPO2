package com.banquito.core.integration.switchapi.mapper;

import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;

import java.util.UUID;

public final class IntegracionSwitchMapper {

    private IntegracionSwitchMapper() {
    }

    public static ValidarEmpresaSwitchResponse toEmpresaNoExisteResponse(String ruc) {
        return new ValidarEmpresaSwitchResponse(
                ruc,
                false,
                null,
                false
        );
    }

    public static ValidarEmpresaSwitchResponse toEmpresaExisteResponse(
            String ruc,
            String estado,
            Boolean activoPagosMasivos
    ) {
        return new ValidarEmpresaSwitchResponse(
                ruc,
                true,
                estado,
                activoPagosMasivos
        );
    }

    public static LiquidacionServicioSwitchResponse toLiquidacionAplicadaResponse(
            UUID uuidDebitoMatriz,
            UUID uuidCreditoIngresos,
            UUID uuidCreditoIva,
            UUID uuidGrupoOperacion
    ) {
        return new LiquidacionServicioSwitchResponse(
                "APLICADA",
                uuidDebitoMatriz,
                uuidCreditoIngresos,
                uuidCreditoIva,
                uuidGrupoOperacion
        );
    }
}