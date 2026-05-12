package com.banquito.core.branches.dto.api;

import jakarta.validation.constraints.NotBlank;

public record SucursalRequest(
        @NotBlank String codigoSucursal,
        @NotBlank String nombre,
        @NotBlank String ciudad,
        String direccion
) {}
