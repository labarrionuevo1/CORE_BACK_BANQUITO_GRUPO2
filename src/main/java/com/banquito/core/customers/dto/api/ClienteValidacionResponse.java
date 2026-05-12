package com.banquito.core.customers.dto.api;

public record ClienteValidacionResponse(
        String ruc,
        boolean esValida,
        String mensaje,
        String motivo
) {}
