package com.banquito.core.transactions.dto.response;

import com.banquito.core.transactions.enums.TipoMovimientoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimientoCuentaResponse(
        Long id,
        UUID uuidTransaccion,
        TipoMovimientoEnum tipoMovimiento,
        BigDecimal monto,
        BigDecimal saldoResultante,
        String descripcion,
        LocalDateTime fechaTransaccion
) {}
