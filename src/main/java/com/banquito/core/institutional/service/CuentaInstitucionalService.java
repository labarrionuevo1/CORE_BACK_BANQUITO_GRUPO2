package com.banquito.core.institutional.service;

import java.util.List;

import com.banquito.core.institutional.dto.api.CuentaInstitucionalResponse;
import com.banquito.core.institutional.model.CuentaInstitucional;

public interface CuentaInstitucionalService {

    List<CuentaInstitucionalResponse> listar();

    CuentaInstitucionalResponse porCodigo(String codigo);

    CuentaInstitucionalResponse porNumeroCuenta(String numeroCuenta);

    CuentaInstitucional obtenerPorCodigo(String codigo);

    CuentaInstitucional obtenerPorNumeroCuenta(String numeroCuenta);

    CuentaInstitucional validarActivaPorCodigo(String codigo);

    CuentaInstitucional validarActivaPorNumeroCuenta(String numeroCuenta);
}