package com.banquito.core.accounts.enums;

import lombok.Getter;

@Getter
public enum TipoBaseCuentaEnum {
    AHORROS("AHORROS"),
    CORRIENTE("CORRIENTE");

    private final String value;

    TipoBaseCuentaEnum(String value) {
        this.value = value;
    }
}
