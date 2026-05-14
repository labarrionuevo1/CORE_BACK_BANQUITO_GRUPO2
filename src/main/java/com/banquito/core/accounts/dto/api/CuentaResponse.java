package com.banquito.core.accounts.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;

import java.math.BigDecimal;

public record CuentaResponse(
        Integer id,
        String numeroCuenta,
        Integer clienteId,
        Integer sucursalId,
        Integer subtipoCuentaId,
        EstadoCuentaEnum estado,
        BigDecimal saldoContable,
        BigDecimal saldoDisponible,
        Boolean permiteSobregiro,
        BigDecimal limiteSobregiro,
        Boolean esFavoritaPagos
) {}
