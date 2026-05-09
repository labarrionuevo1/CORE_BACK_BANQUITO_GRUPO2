package com.banquito.core.institutional.controller;

import com.banquito.core.institutional.service.CuentaInstitucionalService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/cuentas-institucionales")
@RequiredArgsConstructor
public class CuentaInstitucionalController {
    private final CuentaInstitucionalService service;
    @GetMapping public ApiResponse<?> listar() { return ApiResponse.ok("Cuentas institucionales obtenidas", service.listar()); }
    @GetMapping("codigo/{codigo}") public ApiResponse<?> porCodigo(@PathVariable String codigo) { return ApiResponse.ok("Cuenta institucional", service.porCodigo(codigo)); }
}
