package com.banquito.core.shared.enums;

import lombok.Getter;

@Getter
public enum CanalOrigenEnum {
    CORE("CORE"),
    AGENCIA("AGENCIA"),
    WEB("WEB"),
    SWITCH("SWITCH"),
    ATM("ATM"),
    SISTEMA("SISTEMA");

    private final String value;

    CanalOrigenEnum(String value) {
        this.value = value;
    }
}
