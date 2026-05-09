package com.banquito.core.transactions.enums;

import lombok.Getter;

@Getter
public enum EstadoTransaccionEnum {
    PENDIENTE("PENDIENTE"),
    COMPLETADA("COMPLETADA"),
    RECHAZADA("RECHAZADA"),
    REVERSADA("REVERSADA");

    private final String value;

    EstadoTransaccionEnum(String value) {
        this.value = value;
    }
}
