package com.banquito.core.transactions.enums;

import lombok.Getter;

@Getter
public enum TipoMovimientoEnum {
    DEBITO("DEBITO"),
    CREDITO("CREDITO");

    private final String value;

    TipoMovimientoEnum(String value) {
        this.value = value;
    }
}
