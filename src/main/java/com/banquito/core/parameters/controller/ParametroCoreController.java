package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.service.ParametroCoreService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/parametros")
@RequiredArgsConstructor
public class ParametroCoreController {
    private final ParametroCoreService service;
    
    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Parametros obtenidos", service.listar()); 
    }
    
    @GetMapping("/activos")
    public ApiResponse<?> listarActivos() {
        return ApiResponse.ok("Parámetros activos obtenidos", service.listarActivos()); 
    }
    
    @GetMapping("/{codigo}")
    public ApiResponse<?> obtener(@PathVariable String codigo) {
        return ApiResponse.ok("Parametro obtenido", service.obtener(codigo)); 
    }
}
