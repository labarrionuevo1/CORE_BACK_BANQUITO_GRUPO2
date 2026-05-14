package com.banquito.core.integration.switchapi.service;

import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.ValidationException;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;

import java.time.LocalDate;

public interface IntegracionSwitchService {

    ValidarEmpresaSwitchResponse validarEmpresa(String ruc);

    SaldoCuentaResponse consultarDisponibilidad(String numeroCuenta);

    TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request);

    LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request);

    SaldoCuentaResponse validarCuentaDestino(String numeroCuenta);

    LocalDate siguienteDiaHabil(LocalDate fecha);
}