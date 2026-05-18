package com.banquito.core.security.controller;

import com.banquito.core.security.dto.api.UsuarioCoreEstadoRequest;
import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/core/usuarios-core")
@RequiredArgsConstructor
public class UsuarioCoreController {

    private final UsuarioCoreService service;

    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Usuarios core obtenidos", service.listar());
    }

    @GetMapping("/{identificador}")
    public ApiResponse<?> obtener(@PathVariable String identificador) {
        if (esNumero(identificador)) {
            return ApiResponse.ok("Usuario core obtenido", service.obtener(Integer.valueOf(identificador)));
        }

        return ApiResponse.ok("Usuario core obtenido", service.obtenerPorUsername(identificador));
    }

    @GetMapping("/{username}/validacion")
    public ApiResponse<?> validarRolYEstado(
            @PathVariable String username,
            @RequestParam RolUsuarioCoreEnum rol,
            @RequestParam EstadoUsuarioCoreEnum estado
    ) {
        return ApiResponse.ok(
                "Validacion de usuario core completada",
                service.validarRolYEstado(username, rol, estado)
        );
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

    private boolean esNumero(String valor) {
        return valor != null && valor.matches("\\d+");
    }
}