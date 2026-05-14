package com.banquito.core.accounts.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;

import java.math.BigDecimal;

public record SaldoCuentaResponse(
        String numeroCuenta,
        EstadoCuentaEnum estado,
        BigDecimal saldoContable,
        BigDecimal saldoDisponible,
        Boolean permiteDebito
) {}
