package com.banquito.core.accounts.dto.request;

import jakarta.validation.constraints.NotNull;

public record CrearCuentaRequest(
        @NotNull Integer clienteId,
        @NotNull Integer sucursalId,
        @NotNull Integer subtipoCuentaId,
        Boolean permiteSobregiro,
        Boolean esFavoritaPagos
) {}
