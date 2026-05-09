package com.banquito.core.security.enums;

import lombok.Getter;

@Getter
public enum EstadoUsuarioCoreEnum {
    ACTIVO("ACTIVO"),
    BLOQUEADO("BLOQUEADO"),
    INACTIVO("INACTIVO");

    private final String value;

    EstadoUsuarioCoreEnum(String value) {
        this.value = value;
    }
}
