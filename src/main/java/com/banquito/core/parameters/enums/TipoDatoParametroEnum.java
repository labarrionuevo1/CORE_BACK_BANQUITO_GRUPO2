package com.banquito.core.parameters.enums;

import lombok.Getter;

@Getter
public enum TipoDatoParametroEnum {
    NUMERICO("NUMERICO"),
    CADENA("CADENA"),
    FECHA("FECHA"),
    HORA("HORA"),
    BOOLEANO("BOOLEANO");

    private final String value;

    TipoDatoParametroEnum(String value) {
        this.value = value;
    }
}
