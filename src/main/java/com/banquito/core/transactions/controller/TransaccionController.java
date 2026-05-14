package com.banquito.core.transactions.controller;

import java.util.List;
import java.util.UUID;

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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService service;

    @PostMapping("/transferencias")
    public ApiResponse<TransferenciaResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return ApiResponse.ok("Transferencia procesada", service.ejecutarTransferencia(request));
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ApiResponse<List<MovimientoCuentaResponse>> obtenerMovimientosPorCuenta(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Movimientos obtenidos", service.obtenerMovimientosPorCuenta(numeroCuenta));
    }

    @GetMapping("/{uuid}")
    public ApiResponse<MovimientoCuentaResponse> obtenerPorUuid(@PathVariable UUID uuid) {
        return ApiResponse.ok("Transacción obtenida", service.obtenerPorUuid(uuid));
    }
}