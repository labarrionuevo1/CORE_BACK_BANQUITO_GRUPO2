package com.banquito.core.accounts.mapper;

import com.banquito.core.accounts.dto.api.CuentaResponse;
import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.accounts.model.Cuenta;

public final class CuentaMapper {

    private CuentaMapper() {
    }

    public static CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getCliente().getId(),
                cuenta.getSucursal().getId(),
                cuenta.getSubtipoCuenta().getId(),
                cuenta.getEstado(),
                cuenta.getSaldoContable(),
                cuenta.getSaldoDisponible(),
                cuenta.getPermiteSobregiro(),
                cuenta.getLimiteSobregiro(),
                cuenta.getEsFavoritaPagos());
    }

    public static SaldoCuentaResponse toSaldoResponse(Cuenta cuenta) {
        boolean permiteDebito = cuenta.getEstado().name().equals("ACTIVA") && cuenta.getSaldoDisponible().compareTo(java.math.BigDecimal.ZERO) >= 0;
        return new SaldoCuentaResponse(
                cuenta.getNumeroCuenta(),
                cuenta.getEstado(),
                cuenta.getSaldoContable(),
                cuenta.getSaldoDisponible(),
                permiteDebito);
    }
}
