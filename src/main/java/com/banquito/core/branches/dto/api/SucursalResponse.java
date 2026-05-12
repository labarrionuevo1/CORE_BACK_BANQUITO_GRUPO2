package com.banquito.core.branches.dto.api;

import com.banquito.core.branches.enums.EstadoSucursalEnum;

public record SucursalResponse(
    Integer id, 
    String codigoSucursal, 
    String nombre, 
    String ciudad, 
    String direccion, 
    EstadoSucursalEnum estado
) {}
