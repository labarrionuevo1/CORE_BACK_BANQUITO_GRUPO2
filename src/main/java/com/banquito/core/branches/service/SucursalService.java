package com.banquito.core.branches.service;

import com.banquito.core.branches.dto.api.SucursalRequest;
import com.banquito.core.branches.dto.api.SucursalResponse;
import com.banquito.core.branches.model.Sucursal;

import java.util.List;

public interface SucursalService {
    
    List<SucursalResponse> listar();
    
    List<SucursalResponse> listarActivas();
    
    Sucursal obtenerEntidad(Integer id);
    
    SucursalResponse obtener(Integer id);
    
    SucursalResponse obtenerPorCodigo(String codigoSucursal);
    
    SucursalResponse crear(SucursalRequest request);
    
}
