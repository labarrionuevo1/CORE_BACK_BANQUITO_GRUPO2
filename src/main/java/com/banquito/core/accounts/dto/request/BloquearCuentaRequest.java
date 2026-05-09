package com.banquito.core.accounts.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BloquearCuentaRequest(
        @NotNull @DecimalMin("0.01") BigDecimal montoBloqueado,
        @NotBlank String motivo,
        String autoridadOrdenante,
        Integer usuarioCoreId,
        String observaciones
) {}
