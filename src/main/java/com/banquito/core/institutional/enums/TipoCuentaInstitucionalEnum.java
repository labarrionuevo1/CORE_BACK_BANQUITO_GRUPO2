package com.banquito.core.institutional.enums;

import lombok.Getter;

@Getter
public enum TipoCuentaInstitucionalEnum {
    INGRESO("INGRESO"),
    PASIVO("PASIVO"),
    IMPUESTO("IMPUESTO"),
    OPERATIVA("OPERATIVA");

    private final String value;

    TipoCuentaInstitucionalEnum(String value) {
        this.value = value;
    }
}
