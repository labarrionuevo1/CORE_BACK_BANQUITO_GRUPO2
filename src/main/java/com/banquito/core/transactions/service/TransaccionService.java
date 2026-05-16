package com.banquito.core.transactions.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.banquito.core.transactions.dto.api.MovimientoCuentaResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;

public interface TransaccionService {

    TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request);

    UUID debitarCuentaMatrizLiquidacion(
            String numeroCuenta,
            BigDecimal monto,
            UUID uuidGrupoOperacion,
            String referenciaExterna,
            boolean permitirSobregiroLiquidacion
    );

    UUID registrarMovimientoInstitucional(
            String codigoCuenta,
            String codigoSubtipo,
            TipoMovimientoEnum tipo,
            BigDecimal monto,
            UUID grupo,
            String referencia
    );

    List<MovimientoCuentaResponse> obtenerMovimientosPorCuenta(String numeroCuenta);

    MovimientoCuentaResponse obtenerPorUuid(UUID uuid);
}
