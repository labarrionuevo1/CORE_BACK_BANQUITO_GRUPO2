package com.banquito.core.security.enums;

import lombok.Getter;

@Getter
public enum RolUsuarioCoreEnum {
    CAJERO("CAJERO"),
    SUPERVISOR_AGENCIA("SUPERVISOR_AGENCIA"),
    ADMIN_CORE("ADMIN_CORE"),
    AUDITOR("AUDITOR"),
    SISTEMA("SISTEMA");

    private final String value;

    RolUsuarioCoreEnum(String value) {
        this.value = value;
    }
}
