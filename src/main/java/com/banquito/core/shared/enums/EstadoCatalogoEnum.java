package com.banquito.core.shared.enums;

import lombok.Getter;

@Getter
public enum EstadoCatalogoEnum {
    ACTIVO("ACTIVO"),
    INACTIVO("INACTIVO");

    private final String value;

    EstadoCatalogoEnum(String value) {
        this.value = value;
    }
}
