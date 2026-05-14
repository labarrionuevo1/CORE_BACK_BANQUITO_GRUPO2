package com.banquito.core.institutional.mapper;

import com.banquito.core.institutional.dto.api.CuentaInstitucionalResponse;
import com.banquito.core.institutional.model.CuentaInstitucional;

public final class CuentaInstitucionalMapper {
    private CuentaInstitucionalMapper() {}
    public static CuentaInstitucionalResponse toResponse(CuentaInstitucional c) {
        return new CuentaInstitucionalResponse(c.getId(), c.getNumeroCuenta(), c.getCodigo(), c.getNombre(), c.getTipoCuenta(), c.getSaldoContable(), c.getEstado());
    }
}
