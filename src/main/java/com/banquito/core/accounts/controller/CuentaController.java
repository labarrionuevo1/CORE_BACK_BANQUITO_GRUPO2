package com.banquito.core.accounts.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.accounts.dto.api.BloquearCuentaRequest;
import com.banquito.core.accounts.dto.api.CambiarEstadoCuentaRequest;
import com.banquito.core.accounts.dto.api.CrearCuentaRequest;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService service;

    @GetMapping public ApiResponse<?> listar() {
        return ApiResponse.ok("Cuentas obtenidas", service.listar());
    }

    @GetMapping("/cliente/{clienteId}")
    public ApiResponse<?> listarPorCliente(@PathVariable Integer clienteId) {
        return ApiResponse.ok("Cuentas del cliente obtenidas", service.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}") public ApiResponse<?> obtener(@PathVariable Integer id) {
        return ApiResponse.ok("Cuenta obtenida", service.obtener(id));
    }

    @GetMapping("/numero/{numeroCuenta}/saldo") public ApiResponse<?> saldo(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Saldo obtenido", service.saldo(numeroCuenta));
    }

    @PostMapping public ApiResponse<?> crear(@Valid @RequestBody CrearCuentaRequest request) {
        return ApiResponse.ok("Cuenta creada", service.crear(request));
    }

    @PatchMapping("/{id}/estado") public ApiResponse<?> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoCuentaRequest request) {
        return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, request));
    }

    @PostMapping("/{id}/bloqueos") public ApiResponse<?> bloquear(@PathVariable Integer id, @Valid @RequestBody BloquearCuentaRequest request) {
        service.bloquear(id, request);
        return ApiResponse.ok("Bloqueo registrado", null);
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ApiResponse<?> obtenerPorNumero(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Cuenta obtenida", service.obtenerResponsePorNumero(numeroCuenta));
    }

    @PatchMapping("/bloqueos/{id}/liberar")
    public ApiResponse<?> liberarBloqueo(@PathVariable Integer id) {
        service.liberarBloqueo(id);
        return ApiResponse.ok("Bloqueo liberado correctamente", null);
    }
}