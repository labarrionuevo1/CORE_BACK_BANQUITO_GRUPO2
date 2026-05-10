package com.banquito.core.customers.controller;

import com.banquito.core.customers.dto.request.ClienteRequest;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.service.ClienteService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {
    private final ClienteService service;
    
    @GetMapping
    public ApiResponse<?> listar() { 
        log.info("Listando todos los clientes");
        return ApiResponse.ok("Clientes obtenidos", service.listar()); 
    }
    
    @GetMapping("/{id}")
    public ApiResponse<?> obtener(@PathVariable Integer id) { 
        log.info("Obteniendo cliente por ID: {}", id);
        return ApiResponse.ok("Cliente obtenido", service.obtener(id)); 
    }
    
    @GetMapping("/identificacion/{identificacion}")
    public ApiResponse<?> obtenerPorIdentificacion(@PathVariable String identificacion) { 
        log.info("Buscando cliente por identificación: {}", identificacion);
        return ApiResponse.ok("Cliente obtenido", service.obtenerPorIdentificacion(identificacion)); 
    }
    
    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody ClienteRequest request) { 
        log.info("Creando nuevo cliente");
        return ApiResponse.ok("Cliente creado", service.crear(request)); 
    }
    
    @PatchMapping("/{id}/estado")
    public ApiResponse<?> cambiarEstado(@PathVariable Integer id, @RequestBody EstadoRequest request) {
        log.info("Cambiando estado del cliente: {}", id);
        return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, request.estado));
    }
    
    @GetMapping("/ruc/{ruc}/validacion-pagos-masivos")
    public ApiResponse<?> validarEmpresaParaPagosMasivos(@PathVariable String ruc) {
        log.info("Validando empresa para pagos masivos: {}", ruc);
        boolean esValida = service.validarEmpresaParaPagosMasivos(ruc);
        return ApiResponse.ok("Validación completada", esValida);
    }
    
    public record EstadoRequest(EstadoClienteEnum estado) {}
}
