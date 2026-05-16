package com.banquito.core.integration.switchapi.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;

public record ValidarCuentaDestinoSwitchResponse(
        String numeroCuenta,
        String identificacionBeneficiario,
        Boolean existe,
        Boolean perteneceBeneficiario,
        EstadoCuentaEnum estado,
        Boolean permiteDeposito,
        Boolean bloqueada,
        Boolean valida,
        String codigo,
        String mensaje
) {
}