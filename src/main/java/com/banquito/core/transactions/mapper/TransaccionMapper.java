package com.banquito.core.transactions.mapper;

import com.banquito.core.transactions.dto.api.MovimientoCuentaResponse;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.model.TransaccionCuenta;

import java.math.BigDecimal;
import java.util.UUID;

public final class TransaccionMapper {

    private TransaccionMapper() {
    }

    public static MovimientoCuentaResponse toMovimientoResponse(TransaccionCuenta transaccion) {
        return new MovimientoCuentaResponse(
                transaccion.getId(),
                transaccion.getUuidTransaccion(),
                transaccion.getTipoMovimiento(),
                transaccion.getMonto(),
                transaccion.getSaldoResultante(),
                transaccion.getDescripcion(),
                transaccion.getFechaTransaccion(),
                transaccion.getNumeroComprobante()
        );
    }

    public static TransferenciaResponse toTransferenciaResponse(
            UUID uuidDebitoCore,
            UUID uuidCreditoCore,
            UUID uuidGrupoOperacion,
            BigDecimal saldoDisponibleOrigen,
            String numeroComprobante
    ) {
        return new TransferenciaResponse(
                "EXITOSA",
                uuidDebitoCore,
                uuidCreditoCore,
                uuidGrupoOperacion,
                saldoDisponibleOrigen,
                numeroComprobante
        );
    }
}