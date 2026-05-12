package com.banquito.core.customers.dto.api;

import com.banquito.core.customers.enums.EstadoClienteEnum;
import jakarta.validation.constraints.NotNull;

public record ClienteEstadoRequest(
        @NotNull EstadoClienteEnum estado
) {}
