package com.banquito.core.customers.controller;

import com.banquito.core.customers.dto.request.ClienteRequest;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.service.ClienteService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;
    
    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Clientes obtenidos", service.listar()); 
    }
    
    @GetMapping("/{id}")
    public ApiResponse<?> obtener(@PathVariable Integer id) {
        return ApiResponse.ok("Cliente obtenido", service.obtener(id)); 
    }
    
    @GetMapping("/identificacion/{identificacion}")
    public ApiResponse<?> obtenerPorIdentificacion(@PathVariable String identificacion) {
        return ApiResponse.ok("Cliente obtenido", service.obtenerPorIdentificacion(identificacion)); 
    }
    
    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody ClienteRequest request) {
        return ApiResponse.ok("Cliente creado", service.crear(request)); 
    }
    
    @PatchMapping("/{id}/estado")
    public ApiResponse<?> cambiarEstado(@PathVariable Integer id, @RequestBody EstadoRequest request) {
        return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, request.estado));
    }
    
    @GetMapping("/ruc/{ruc}/validacion-pagos-masivos")
    public ApiResponse<?> validarEmpresaParaPagosMasivos(@PathVariable String ruc) {
        boolean esValida = service.validarEmpresaParaPagosMasivos(ruc);
        return ApiResponse.ok("Validación completada", esValida);
    }
    
    public record EstadoRequest(EstadoClienteEnum estado) {}
}
