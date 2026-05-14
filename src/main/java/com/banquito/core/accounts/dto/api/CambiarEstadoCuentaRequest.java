package com.banquito.core.accounts.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuentaRequest(
        @NotNull EstadoCuentaEnum nuevoEstado,
        @NotBlank String motivoCambio,
        Integer usuarioCoreId
) {}
