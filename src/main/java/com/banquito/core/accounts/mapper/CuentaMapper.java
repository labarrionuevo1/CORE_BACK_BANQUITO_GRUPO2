package com.banquito.core.accounts.mapper;

import com.banquito.core.accounts.dto.api.CuentaResponse;
import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.accounts.model.Cuenta;

public final class CuentaMapper {
    private CuentaMapper() {}
    public static CuentaResponse toResponse(Cuenta c) {
        return new CuentaResponse(c.getId(), c.getNumeroCuenta(), c.getCliente().getId(), c.getSucursal().getId(), c.getSubtipoCuenta().getId(),
                c.getEstado(), c.getSaldoContable(), c.getSaldoDisponible(), c.getPermiteSobregiro(), c.getLimiteSobregiro(), c.getEsFavoritaPagos());
    }
    public static SaldoCuentaResponse toSaldoResponse(Cuenta c) {
        boolean permiteDebito = c.getEstado().name().equals("ACTIVA") && c.getSaldoDisponible().compareTo(java.math.BigDecimal.ZERO) >= 0;
        return new SaldoCuentaResponse(c.getNumeroCuenta(), c.getEstado(), c.getSaldoContable(), c.getSaldoDisponible(), permiteDebito);
    }
}
