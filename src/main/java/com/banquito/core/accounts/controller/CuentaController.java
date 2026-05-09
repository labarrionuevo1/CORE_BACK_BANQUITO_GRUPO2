package com.banquito.core.accounts.controller;

import com.banquito.core.accounts.dto.request.BloquearCuentaRequest;
import com.banquito.core.accounts.dto.request.CambiarEstadoCuentaRequest;
import com.banquito.core.accounts.dto.request.CrearCuentaRequest;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService service;
    @GetMapping public ApiResponse<?> listar() { return ApiResponse.ok("Cuentas obtenidas", service.listar()); }
    @GetMapping("/{id}") public ApiResponse<?> obtener(@PathVariable Integer id) { return ApiResponse.ok("Cuenta obtenida", service.obtener(id)); }
    @GetMapping("numero/{numeroCuenta}/saldo") public ApiResponse<?> saldo(@PathVariable String numeroCuenta) { return ApiResponse.ok("Saldo obtenido", service.saldo(numeroCuenta)); }
    @PostMapping public ApiResponse<?> crear(@Valid @RequestBody CrearCuentaRequest request) { return ApiResponse.ok("Cuenta creada", service.crear(request)); }
    @PatchMapping("/{id}/estado") public ApiResponse<?> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoCuentaRequest request) { return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, request)); }
    @PostMapping("/{id}/bloqueos") public ApiResponse<?> bloquear(@PathVariable Integer id, @Valid @RequestBody BloquearCuentaRequest request) { service.bloquear(id, request); return ApiResponse.ok("Bloqueo registrado", null); }
}
