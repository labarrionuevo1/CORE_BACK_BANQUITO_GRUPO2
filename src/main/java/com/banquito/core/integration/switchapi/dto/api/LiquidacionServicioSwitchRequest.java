package com.banquito.core.integration.switchapi.dto.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LiquidacionServicioSwitchRequest(

        @NotNull(message = "El UUID de grupo de operacion es obligatorio")
        UUID uuidGrupoOperacion,

        @NotBlank(message = "La cuenta matriz es obligatoria")
        String cuentaMatriz,

        @NotNull(message = "El subtotal de comision es obligatorio")
        @DecimalMin(value = "0.00", message = "El subtotal de comision no puede ser negativo")
        BigDecimal subtotalComision,

        @NotNull(message = "El monto IVA es obligatorio")
        @DecimalMin(value = "0.00", message = "El monto IVA no puede ser negativo")
        BigDecimal montoIva,

        @NotNull(message = "El total debitado es obligatorio")
        @DecimalMin(value = "0.00", message = "El total debitado no puede ser negativo")
        BigDecimal totalDebitado,

        Boolean permiteSobregiro,

        @NotBlank(message = "El codigo de cuenta de ingresos es obligatorio")
        String codigoCuentaIngresos,

        @NotBlank(message = "El codigo de cuenta IVA es obligatorio")
        String codigoCuentaIva,

        String referenciaExterna
) {
}