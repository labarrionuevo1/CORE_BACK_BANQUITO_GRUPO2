package com.banquito.core.security.controller;

import com.banquito.core.security.service.AuthService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @GetMapping("/credenciales/{usuario}")
    public ApiResponse<?> buscarCredencialWeb(@PathVariable String usuario) { return ApiResponse.ok("Credencial web", service.buscarCredencialWeb(usuario)); }
    @GetMapping("/usuarios-core/{usuario}")
    public ApiResponse<?> buscarUsuarioCore(@PathVariable String usuario) { return ApiResponse.ok("Usuario core", service.buscarUsuarioCore(usuario)); }
}
