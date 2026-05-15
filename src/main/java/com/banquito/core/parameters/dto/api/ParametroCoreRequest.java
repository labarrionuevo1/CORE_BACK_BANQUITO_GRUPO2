package com.banquito.core.parameters.dto.api;

import jakarta.validation.constraints.NotBlank;

public record ParametroCoreRequest(
    @NotBlank(message = "El código es obligatorio")
    String codigo,

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El valor es obligatorio")
    String valor,

    @NotBlank(message = "El tipo de dato es obligatorio")
    String tipoDato,

    String descripcion
) {}
