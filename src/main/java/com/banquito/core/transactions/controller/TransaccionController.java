package com.banquito.core.transactions.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.api.MovimientoCuentaResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.service.TransaccionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/core/transacciones")
public class TransaccionController {

    private final TransaccionService service;

    public TransaccionController(TransaccionService service) {
        this.service = service;
    }

    @PostMapping("/transferencias")
    @PreAuthorize("(hasRole('CAJERO') or hasRole('SISTEMA') or hasRole('SUPERVISOR_AGENCIA')) and @transaccionSecurityService.puedeCrearTransaccion(authentication, #request)")
    public ApiResponse<TransferenciaResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return ApiResponse.ok("Transferencia procesada", service.ejecutarTransferencia(request));
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR_AGENCIA', 'AUDITOR', 'ADMIN_CORE', 'SISTEMA')")
    public ApiResponse<List<MovimientoCuentaResponse>> obtenerMovimientosPorCuenta(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Movimientos obtenidos", service.obtenerMovimientosPorCuenta(numeroCuenta));
    }

    @GetMapping("/{identificador}")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR_AGENCIA', 'AUDITOR', 'ADMIN_CORE', 'SISTEMA')")
    public ApiResponse<List<MovimientoCuentaResponse>> obtenerPorIdentificador(@PathVariable String identificador) {
        return ApiResponse.ok("Transacciones obtenidas", service.obtenerPorIdentificador(identificador));
    }
}