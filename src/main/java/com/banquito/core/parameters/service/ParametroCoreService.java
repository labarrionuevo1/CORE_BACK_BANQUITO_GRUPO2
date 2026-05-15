package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.api.ParametroCoreRequest;
import com.banquito.core.parameters.dto.api.ParametroCoreResponse;

import java.util.List;

public interface ParametroCoreService {

    List<ParametroCoreResponse> listar();

    ParametroCoreResponse obtener(String codigo);

    ParametroCoreResponse crear(ParametroCoreRequest request);

    ParametroCoreResponse actualizar(String codigo, ParametroCoreRequest request);

    void eliminar(String codigo);
}
