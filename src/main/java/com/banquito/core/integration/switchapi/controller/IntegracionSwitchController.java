package com.banquito.core.integration.switchapi.controller;

import com.banquito.core.integration.switchapi.dto.api.CuentaFavoritaPagosResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.api.LoginRequest;
import com.banquito.core.integration.switchapi.dto.api.LoginResponse;
import com.banquito.core.integration.switchapi.service.IntegracionSwitchService;
import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/core/integracion-switch")
@RequiredArgsConstructor
public class IntegracionSwitchController {

    private final IntegracionSwitchService service;

    @GetMapping("/empresas/{ruc}/validacion")
    public ApiResponse<?> validarEmpresa(@PathVariable String ruc) {
        return ApiResponse.ok("Validación de empresa", service.validarEmpresa(ruc));
    }

    @GetMapping("/empresas/{ruc}/cuentas/{numeroCuenta}/validacion-matriz")
    public ApiResponse<?> validarCuentaMatriz(
            @PathVariable String ruc,
            @PathVariable String numeroCuenta
    ) {
        return ApiResponse.ok(
                "Validación de cuenta matriz completada",
                service.validarCuentaMatriz(ruc, numeroCuenta)
        );
    }

    @GetMapping("/empresas/{ruc}/credenciales/{username}/validacion")
    public ApiResponse<?> validarCredencialEmpresarial(
            @PathVariable String ruc,
            @PathVariable String username
    ) {
        return ApiResponse.ok(
                "Validación de credencial empresarial completada",
                service.validarCredencialEmpresarial(ruc, username)
        );
    }

    @GetMapping("/empresas/{ruc}/cuenta-favorita-pagos")
    public ApiResponse<CuentaFavoritaPagosResponse> consultarCuentaFavoritaPagos(@PathVariable String ruc) {
        return ApiResponse.ok(
                "Consulta de cuenta favorita para pagos completada",
                service.consultarCuentaFavoritaPagos(ruc)
        );
    }

    @PostMapping("/autenticacion/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(
                "Autenticación Switch procesada",
                service.login(request)
        );
    }

    @GetMapping("/cuentas/{numeroCuenta}/disponibilidad")
    public ApiResponse<?> disponibilidad(@PathVariable String numeroCuenta) {
        return ApiResponse.ok(
                "Disponibilidad de cuenta",
                service.consultarDisponibilidad(numeroCuenta)
        );
    }

    @GetMapping("/cuentas/{numeroCuenta}/validacion-destino")
    public ApiResponse<?> validarCuentaDestino(
            @PathVariable String numeroCuenta,
            @RequestParam(required = false) String identificacionBeneficiario
    ) {
        return ApiResponse.ok(
                "Validación de cuenta destino completada",
                service.validarCuentaDestino(numeroCuenta, identificacionBeneficiario)
        );
    }

    @PostMapping("/transacciones/transferencia")
    public ApiResponse<?> transferencia(@Valid @RequestBody TransferenciaRequest request) {
        return ApiResponse.ok(
                "Transferencia Switch procesada",
                service.ejecutarTransferencia(request)
        );
    }

    @PostMapping("/transacciones/liquidacion-servicio")
    public ApiResponse<?> liquidacion(@Valid @RequestBody LiquidacionServicioSwitchRequest request) {
        return ApiResponse.ok(
                "Liquidación procesada",
                service.liquidarServicio(request)
        );
    }

    @GetMapping("/feriados/siguiente-dia-habil")
    public ApiResponse<?> siguienteDiaHabil(@RequestParam LocalDate fecha) {
        return ApiResponse.ok(
                "Siguiente día hábil",
                service.siguienteDiaHabil(fecha)
        );
    }

    @GetMapping("/calendario/dia-habil")
    public ApiResponse<?> consultarDiaHabil(@RequestParam LocalDate fecha) {
        return ApiResponse.ok(
                "Consulta de día hábil completada",
                service.consultarDiaHabil(fecha)
        );
    }
}