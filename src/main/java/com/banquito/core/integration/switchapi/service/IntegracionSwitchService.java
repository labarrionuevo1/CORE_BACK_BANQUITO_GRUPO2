package com.banquito.core.integration.switchapi.service;

import java.time.LocalDate;

import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.integration.switchapi.dto.api.CuentaFavoritaPagosResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.api.LoginRequest;
import com.banquito.core.integration.switchapi.dto.api.LoginResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.DiaHabilSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCredencialEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaDestinoSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaMatrizSwitchResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;

public interface IntegracionSwitchService {

    ValidarEmpresaSwitchResponse validarEmpresa(String ruc);

    ValidarCredencialEmpresaSwitchResponse validarCredencialEmpresarial(String ruc, String username);

    ValidarCuentaMatrizSwitchResponse validarCuentaMatriz(String ruc, String numeroCuenta);

    ValidarCuentaDestinoSwitchResponse validarCuentaDestino(String numeroCuenta, String identificacionBeneficiario);

    SaldoCuentaResponse consultarDisponibilidad(String numeroCuenta);

    CuentaFavoritaPagosResponse consultarCuentaFavoritaPagos(String ruc);

    LoginResponse login(LoginRequest request);

    TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request);

    LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request);

    DiaHabilSwitchResponse consultarDiaHabil(LocalDate fecha);

    LocalDate siguienteDiaHabil(LocalDate fecha);
}