package com.banquito.core.audit.enums;

import lombok.Getter;

@Getter
public enum ResultadoAuditoriaEnum {
    EXITOSO("EXITOSO"),
    FALLIDO("FALLIDO"),
    RECHAZADO("RECHAZADO");

    private final String value;

    ResultadoAuditoriaEnum(String value) {
        this.value = value;
    }
}
