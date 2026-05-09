package com.banquito.core.security.enums;

import lombok.Getter;

@Getter
public enum EstadoCredencialWebEnum {
    ACTIVO("ACTIVO"),
    BLOQUEADO("BLOQUEADO"),
    EXPIRADO("EXPIRADO"),
    INACTIVO("INACTIVO");

    private final String value;

    EstadoCredencialWebEnum(String value) {
        this.value = value;
    }
}
