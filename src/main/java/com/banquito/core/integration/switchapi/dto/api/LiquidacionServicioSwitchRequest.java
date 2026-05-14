package com.banquito.core.integration.switchapi.dto.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LiquidacionServicioSwitchRequest(
        @NotNull UUID uuidGrupoOperacion,
        @NotBlank String cuentaMatriz,
        @NotNull @DecimalMin("0.00") BigDecimal subtotalComision,
        @NotNull @DecimalMin("0.00") BigDecimal montoIva,
        @NotNull @DecimalMin("0.00") BigDecimal totalDebitado,
        Boolean permiteSobregiro,
        @NotBlank String codigoCuentaIngresos,
        @NotBlank String codigoCuentaIva,
        String referenciaExterna
) {}
