package com.banquito.core.branches.mapper;

import com.banquito.core.branches.dto.api.SucursalResponse;
import com.banquito.core.branches.model.Sucursal;

public final class SucursalMapper {
    private SucursalMapper() {}
    public static SucursalResponse toResponse(Sucursal s) {
        return new SucursalResponse(s.getId(), s.getCodigoSucursal(), s.getNombre(), s.getCiudad(), s.getDireccion(), s.getEstado());
    }
}
