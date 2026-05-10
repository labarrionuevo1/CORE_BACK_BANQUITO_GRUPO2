package com.banquito.core.transactions.controller;

import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import com.banquito.core.transactions.service.MotorTransaccionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/core/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final MotorTransaccionalService service;

    @PostMapping("/transferencias")
    public ApiResponse<?> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return ApiResponse.ok("Transferencia procesada", service.ejecutarTransferencia(request));
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ApiResponse<?> obtenerMovimientosPorCuenta(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Movimientos obtenidos", service.obtenerMovimientosPorCuenta(numeroCuenta));
    }

    @GetMapping("/{uuid}")
    public ApiResponse<?> obtenerPorUuid(@PathVariable UUID uuid) {
        return ApiResponse.ok("Transacción obtenida", service.obtenerPorUuid(uuid));
    }
}