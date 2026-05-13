package com.banquito.core.institutional.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.institutional.service.CuentaInstitucionalService;
import com.banquito.core.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/cuentas-institucionales")
@RequiredArgsConstructor
public class CuentaInstitucionalController {

    private final CuentaInstitucionalService service;

    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Cuentas institucionales obtenidas", service.listar());
    }

    @GetMapping("/{numeroCuenta}")
    public ApiResponse<?> porNumeroCuenta(@PathVariable String numeroCuenta) {
        return ApiResponse.ok("Cuenta institucional obtenida", service.porNumeroCuenta(numeroCuenta));
    }

    @GetMapping("/codigo/{codigo}")
    public ApiResponse<?> porCodigo(@PathVariable String codigo) {
        return ApiResponse.ok("Cuenta institucional obtenida", service.porCodigo(codigo));
    }
}