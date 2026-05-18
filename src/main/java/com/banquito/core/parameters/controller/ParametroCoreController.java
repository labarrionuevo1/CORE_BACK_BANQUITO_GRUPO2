package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.dto.api.ParametroCoreRequest;
import com.banquito.core.parameters.service.ParametroCoreService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/parametros")
@RequiredArgsConstructor
public class ParametroCoreController {

    private final ParametroCoreService service;

    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Parámetros obtenidos", service.listar());
    }

    @GetMapping("/activos")
    public ApiResponse<?> listarActivos() {
        return ApiResponse.ok("Parámetros activos obtenidos", service.listarActivos());
    }

    @GetMapping("/{codigo}")
    public ApiResponse<?> obtener(@PathVariable String codigo) {
        return ApiResponse.ok("Parámetro obtenido", service.obtener(codigo));
    }

    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody ParametroCoreRequest request) {
        return ApiResponse.ok("Parámetro creado", service.crear(request));
    }

    @PutMapping("/{codigo}")
    public ApiResponse<?> actualizar(@PathVariable String codigo, @Valid @RequestBody ParametroCoreRequest request) {
        return ApiResponse.ok("Parámetro actualizado", service.actualizar(codigo, request));
    }
}