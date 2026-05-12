package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.service.ParametroCoreService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/parametros")
@RequiredArgsConstructor
@Slf4j
public class ParametroCoreController {
    private final ParametroCoreService service;
    
    @GetMapping
    public ApiResponse<?> listar() { 
        log.info("Listando todos los parámetros");
        return ApiResponse.ok("Parametros obtenidos", service.listar()); 
    }
    
    @GetMapping("/activos")
    public ApiResponse<?> listarActivos() { 
        log.info("Listando parámetros activos");
        return ApiResponse.ok("Parámetros activos obtenidos", service.listarActivos()); 
    }
    
    @GetMapping("/{codigo}")
    public ApiResponse<?> obtener(@PathVariable String codigo) { 
        log.info("Obteniendo parámetro por código: {}", codigo);
        return ApiResponse.ok("Parametro obtenido", service.obtener(codigo)); 
    }
}
