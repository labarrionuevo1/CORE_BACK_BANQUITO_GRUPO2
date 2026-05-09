package com.banquito.core.accounts.enums;

import lombok.Getter;

@Getter
public enum EstadoBloqueoCuentaEnum {
    ACTIVO("ACTIVO"),
    LIBERADO("LIBERADO");

    private final String value;

    EstadoBloqueoCuentaEnum(String value) {
        this.value = value;
    }
}
