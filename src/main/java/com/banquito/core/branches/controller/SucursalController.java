package com.banquito.core.branches.controller;

import com.banquito.core.branches.dto.request.SucursalRequest;
import com.banquito.core.branches.service.SucursalService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/sucursales")
@RequiredArgsConstructor
@Slf4j
public class SucursalController {
    private final SucursalService service;
    
    @GetMapping
    public ApiResponse<?> listar() { 
        log.info("Listando todas las sucursales");
        return ApiResponse.ok("Sucursales obtenidas", service.listar()); 
    }
    
    @GetMapping("/activas")
    public ApiResponse<?> listarActivas() { 
        log.info("Listando sucursales activas");
        return ApiResponse.ok("Sucursales activas obtenidas", service.listarActivas()); 
    }
    
    @GetMapping("/{id}")
    public ApiResponse<?> obtener(@PathVariable Integer id) { 
        log.info("Obteniendo sucursal por ID: {}", id);
        return ApiResponse.ok("Sucursal obtenida", service.obtener(id)); 
    }
    
    @GetMapping("/codigo/{codigoSucursal}")
    public ApiResponse<?> obtenerPorCodigo(@PathVariable String codigoSucursal) { 
        log.info("Obteniendo sucursal por código: {}", codigoSucursal);
        return ApiResponse.ok("Sucursal obtenida", service.obtenerPorCodigo(codigoSucursal)); 
    }
    
    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody SucursalRequest request) { 
        log.info("Creando sucursal: {}", request.codigoSucursal());
        return ApiResponse.ok("Sucursal creada", service.crear(request)); 
    }
}
