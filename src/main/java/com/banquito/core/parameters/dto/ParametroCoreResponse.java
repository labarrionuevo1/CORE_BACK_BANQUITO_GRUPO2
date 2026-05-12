package com.banquito.core.parameters.dto;

import com.banquito.core.parameters.enums.TipoDatoParametroEnum;

public record ParametroCoreResponse(String codigo, String nombre, String valorTexto, TipoDatoParametroEnum tipoDato, String descripcion) {}
