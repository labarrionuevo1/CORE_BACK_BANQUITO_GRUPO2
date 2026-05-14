package com.banquito.core.accounts.dto.api;

import jakarta.validation.constraints.NotNull;

public record CrearCuentaRequest(
        @NotNull Integer clienteId,
        @NotNull Integer sucursalId,
        @NotNull Integer subtipoCuentaId,
        Boolean permiteSobregiro,
        Boolean esFavoritaPagos
) {}
