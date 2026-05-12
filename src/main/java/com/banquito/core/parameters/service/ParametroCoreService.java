package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.api.ParametroCoreResponse;

import java.util.List;

public interface ParametroCoreService {
    
    List<ParametroCoreResponse> listar();
    
    List<ParametroCoreResponse> listarActivos();
    
    ParametroCoreResponse obtener(String codigo);
    
}
