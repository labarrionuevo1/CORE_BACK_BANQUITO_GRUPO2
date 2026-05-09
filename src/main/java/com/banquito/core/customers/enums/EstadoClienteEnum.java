package com.banquito.core.customers.enums;

import lombok.Getter;

@Getter
public enum EstadoClienteEnum {
    ACTIVO("ACTIVO"),
    INACTIVO("INACTIVO"),
    SUSPENDIDO("SUSPENDIDO");

    private final String value;

    EstadoClienteEnum(String value) {
        this.value = value;
    }
}
