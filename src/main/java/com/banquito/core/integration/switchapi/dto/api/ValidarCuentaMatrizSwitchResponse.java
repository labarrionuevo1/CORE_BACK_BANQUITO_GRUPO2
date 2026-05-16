package com.banquito.core.integration.switchapi.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;

import java.math.BigDecimal;

public record ValidarCuentaMatrizSwitchResponse(
        String numeroCuenta,
        String rucEmpresa,
        Boolean existe,
        Boolean perteneceEmpresa,
        EstadoCuentaEnum estado,
        Boolean permiteDebito,
        BigDecimal saldoContable,
        BigDecimal saldoDisponible,
        Boolean permiteSobregiro,
        BigDecimal limiteSobregiro,
        Boolean valida,
        String codigo,
        String mensaje
) {
}