package com.banquito.core.transactions.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequest(
        @NotNull UUID uuidOperacion,
        UUID uuidGrupoOperacion,
        @NotBlank String cuentaOrigen,
        @NotBlank String cuentaDestino,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @NotBlank String codigoSubtipo,
        String referenciaExterna,
        String descripcion
) {}
