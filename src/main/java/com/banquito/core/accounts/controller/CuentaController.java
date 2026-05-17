package com.banquito.core.accounts.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/v1/core/cuentas")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CORE', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Cuentas obtenidas", service.listar());
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR_AGENCIA', 'ADMIN_CORE', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<?> listarPorCliente(@PathVariable Integer clienteId) {
        return ApiResponse.ok("Cuentas del cliente obtenidas", service.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_CORE', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<?> obtener(@PathVariable Integer id) {
        return ApiResponse.ok("Cuenta obtenida", service.obtener(id));
    }

    @GetMapping("/numero/{numeroCuenta}/saldo")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR_AGENCIA', 'ADMIN_CORE', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<?> saldo(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Saldo obtenido", service.saldo(numeroCuenta));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CORE')")
    public ApiResponse<?> crear(@Valid @RequestBody CrearCuentaRequest request) {
        return ApiResponse.ok("Cuenta creada", service.crear(request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("(hasRole('SUPERVISOR_AGENCIA') and @cuentaSecurityService.puedeCambiarEstadoLocal(authentication, #id)) or hasRole('ADMIN_CORE')")
    public ApiResponse<?> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambiarEstadoCuentaRequest request) {
        return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, request));
    }

    @PostMapping("/{id}/bloqueos")
    @PreAuthorize("hasAnyRole('ADMIN_CORE', 'SUPERVISOR_AGENCIA')")
    public ApiResponse<?> bloquear(@PathVariable Integer id, @Valid @RequestBody BloquearCuentaRequest request) {
        service.bloquear(id, request);
        return ApiResponse.ok("Bloqueo registrado", null);
    }

    @GetMapping("/numero/{numeroCuenta}")
    @PreAuthorize("hasAnyRole('CAJERO', 'SUPERVISOR_AGENCIA', 'ADMIN_CORE', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<?> obtenerPorNumero(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Cuenta obtenida", service.obtenerResponsePorNumero(numeroCuenta));
    }

    @PatchMapping("/bloqueos/{id}/liberar")
    @PreAuthorize("hasAnyRole('ADMIN_CORE', 'SUPERVISOR_AGENCIA')")
    public ApiResponse<?> liberarBloqueo(@PathVariable Integer id) {
        service.liberarBloqueo(id);
        return ApiResponse.ok("Bloqueo liberado correctamente", null);
    }
}