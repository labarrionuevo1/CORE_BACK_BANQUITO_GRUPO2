package com.banquito.core.integration.switchapi.controller;

import com.banquito.core.integration.switchapi.dto.request.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.service.IntegracionSwitchService;
import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/core/integracion-switch")
@RequiredArgsConstructor
public class IntegracionSwitchController {

    private final IntegracionSwitchService service;

    @GetMapping("/empresas/{ruc}/validacion")
    public ApiResponse<?> validarEmpresa(@PathVariable String ruc) {
        return ApiResponse.ok("Validacion de empresa", service.validarEmpresa(ruc));
    }

    @GetMapping("/cuentas/{numeroCuenta}/disponibilidad")
    public ApiResponse<?> disponibilidad(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Disponibilidad de cuenta", service.consultarDisponibilidad(numeroCuenta));
    }

    @GetMapping("/cuentas/{numeroCuenta}/validacion-destino")
    public ApiResponse<?> validarCuentaDestino(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Validacion de cuenta destino", service.validarCuentaDestino(numeroCuenta));
    }

    @PostMapping("/transacciones/transferencia")
    public ApiResponse<?> transferencia(@Valid @RequestBody TransferenciaRequest request) {
        return ApiResponse.ok("Transferencia Switch procesada", service.ejecutarTransferencia(request));
    }

    @PostMapping("/transacciones/liquidacion-servicio")
    public ApiResponse<?> liquidacion(@Valid @RequestBody LiquidacionServicioSwitchRequest request) {
        return ApiResponse.ok("Liquidacion procesada", service.liquidarServicio(request));
    }

    @GetMapping("/feriados/siguiente-dia-habil")
    public ApiResponse<?> siguienteDiaHabil(@RequestParam LocalDate fecha) {
        return ApiResponse.ok("Siguiente dia habil", service.siguienteDiaHabil(fecha));
    }
}