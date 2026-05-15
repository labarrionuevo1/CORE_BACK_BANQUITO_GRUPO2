package com.banquito.core.security.controller;

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

    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody UsuarioCoreRequest request) {
        return ApiResponse.ok("Usuario core creado", service.crear(request));
    }
}
