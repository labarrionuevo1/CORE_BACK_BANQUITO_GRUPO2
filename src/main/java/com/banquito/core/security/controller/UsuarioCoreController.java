package com.banquito.core.security.controller;

import com.banquito.core.security.dto.api.UsuarioCoreEstadoRequest;
import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core/usuarios-core")
@RequiredArgsConstructor
public class UsuarioCoreController {

    private final UsuarioCoreService service;

    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Usuarios core obtenidos", service.listar());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> obtener(@PathVariable Integer id) {
        return ApiResponse.ok("Usuario core obtenido", service.obtener(id));
    }

    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody UsuarioCoreRequest request) {
        return ApiResponse.ok("Usuario core creado", service.crear(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioCoreRequest request) {
        return ApiResponse.ok("Usuario core actualizado", service.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ApiResponse<?> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioCoreEstadoRequest request
    ) {
        return ApiResponse.ok("Estado de usuario core actualizado", service.cambiarEstado(id, request.estado()));
    }
}
