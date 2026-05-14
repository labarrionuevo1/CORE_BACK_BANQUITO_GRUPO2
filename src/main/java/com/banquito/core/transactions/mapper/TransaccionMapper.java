package com.banquito.core.transactions.mapper;

import com.banquito.core.transactions.dto.api.MovimientoCuentaResponse;
import com.banquito.core.transactions.model.TransaccionCuenta;

public final class TransaccionMapper {
    private TransaccionMapper() {}

    public static MovimientoCuentaResponse toMovimientoResponse(TransaccionCuenta transaccion) {
        return new MovimientoCuentaResponse(
                transaccion.getId(),
                transaccion.getUuidTransaccion(),
                transaccion.getTipoMovimiento(),
                transaccion.getMonto(),
                transaccion.getSaldoResultante(),
                transaccion.getDescripcion(),
                transaccion.getFechaTransaccion()
        );
    }
}