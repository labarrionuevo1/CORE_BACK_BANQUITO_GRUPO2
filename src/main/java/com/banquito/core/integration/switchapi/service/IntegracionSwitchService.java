package com.banquito.core.integration.switchapi.service;

import com.banquito.core.accounts.dto.response.SaldoCuentaResponse;
import com.banquito.core.integration.switchapi.dto.request.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.response.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.response.ValidarEmpresaSwitchResponse;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import com.banquito.core.transactions.dto.response.TransferenciaResponse;

import java.time.LocalDate;

public interface IntegracionSwitchService {

    ValidarEmpresaSwitchResponse validarEmpresa(String ruc);

    SaldoCuentaResponse consultarDisponibilidad(String numeroCuenta);

    TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request);

    LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request);

    SaldoCuentaResponse validarCuentaDestino(String numeroCuenta);

    LocalDate siguienteDiaHabil(LocalDate fecha);
}