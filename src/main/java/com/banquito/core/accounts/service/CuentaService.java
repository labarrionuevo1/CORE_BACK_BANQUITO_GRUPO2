package com.banquito.core.accounts.service;

import java.util.List;

import com.banquito.core.accounts.dto.api.BloquearCuentaRequest;
import com.banquito.core.accounts.dto.api.CambiarEstadoCuentaRequest;
import com.banquito.core.accounts.dto.api.CrearCuentaRequest;
import com.banquito.core.accounts.dto.api.CuentaResponse;
import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.accounts.model.Cuenta;

public interface CuentaService {

    List<CuentaResponse> listar();

    List<CuentaResponse> listarPorCliente(Integer clienteId);

    Cuenta obtenerEntidad(Integer id);

    Cuenta obtenerPorNumero(String numeroCuenta);

    CuentaResponse obtenerResponsePorNumero(String numeroCuenta);

    CuentaResponse obtener(Integer id);

    SaldoCuentaResponse saldo(String numeroCuenta);

    CuentaResponse crear(CrearCuentaRequest request);

    CuentaResponse cambiarEstado(Integer id, CambiarEstadoCuentaRequest request);

    void bloquear(Integer id, BloquearCuentaRequest request);

    void liberarBloqueo(Integer idBloqueo);
}

