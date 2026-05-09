package com.banquito.core.accounts.enums;

import lombok.Getter;

@Getter
public enum EstadoCuentaEnum {
    ACTIVA("ACTIVA"),
    INACTIVA("INACTIVA"),
    BLOQUEADA("BLOQUEADA"),
    SUSPENDIDA("SUSPENDIDA");

    private final String value;

    EstadoCuentaEnum(String value) {
        this.value = value;
    }
}
