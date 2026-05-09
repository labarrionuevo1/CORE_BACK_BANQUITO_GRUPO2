package com.banquito.core.branches.enums;

import lombok.Getter;

@Getter
public enum EstadoSucursalEnum {
    ACTIVA("ACTIVA"),
    INACTIVA("INACTIVA");

    private final String value;

    EstadoSucursalEnum(String value) {
        this.value = value;
    }
}
