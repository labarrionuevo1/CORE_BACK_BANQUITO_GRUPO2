package com.banquito.core.accounts.dto.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BloquearCuentaRequest(

        @NotNull(message = "El monto bloqueado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto bloqueado debe ser mayor a cero")
        BigDecimal montoBloqueado,

        @NotBlank(message = "El motivo del bloqueo es obligatorio")
        String motivo,

        String autoridadOrdenante,

        String observaciones,

        @NotNull(message = "El usuario core es obligatorio")
        Integer usuarioCoreId
) {
}