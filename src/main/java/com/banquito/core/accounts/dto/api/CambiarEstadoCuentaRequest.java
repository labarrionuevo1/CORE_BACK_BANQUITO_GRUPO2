package com.banquito.core.accounts.dto.api;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuentaRequest(

        @NotNull(message = "El nuevo estado de la cuenta es obligatorio")
        EstadoCuentaEnum nuevoEstado,

        @NotBlank(message = "El motivo del cambio es obligatorio")
        String motivoCambio,

        @NotNull(message = "El usuario core es obligatorio")
        Integer usuarioCoreId
) {
}