package com.banquito.core.integration.switchapi.dto.api;

import java.math.BigDecimal;

public record CuentaFavoritaPagosResponse(
        String rucEmpresa,
        Boolean existe,
        String numeroCuenta,
        String estado,
        Boolean permiteDebito,
        BigDecimal saldoDisponible,
        Boolean esFavoritaPagos,
        Boolean valida,
        String nombreBeneficiario,
        String codigo,
        String mensaje
) {
}
