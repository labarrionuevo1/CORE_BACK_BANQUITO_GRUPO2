package com.banquito.core.branches.controller;

import com.banquito.core.branches.dto.api.SucursalRequest;
import com.banquito.core.branches.dto.api.SucursalResponse;
import com.banquito.core.branches.service.SucursalService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/sucursales")
@RequiredArgsConstructor
public class SucursalController {
    private final SucursalService service;
    
    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Sucursales obtenidas", service.listar()); 
    }
    
    @GetMapping("/activas")
    public ApiResponse<?> listarActivas() {
        return ApiResponse.ok("Sucursales activas obtenidas", service.listarActivas()); 
    }
    
    @GetMapping("/{id}")
    public ApiResponse<?> obtener(@PathVariable Integer id) {
        return ApiResponse.ok("Sucursal obtenida", service.obtener(id)); 
    }
    
    @GetMapping("/codigo/{codigoSucursal}")
    public ApiResponse<?> obtenerPorCodigo(@PathVariable String codigoSucursal) {
        return ApiResponse.ok("Sucursal obtenida", service.obtenerPorCodigo(codigoSucursal)); 
    }
    
    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody SucursalRequest request) {
        return ApiResponse.ok("Sucursal creada", service.crear(request)); 
    }
}
