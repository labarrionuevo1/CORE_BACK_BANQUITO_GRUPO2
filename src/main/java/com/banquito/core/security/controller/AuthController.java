package com.banquito.core.security.controller;

import com.banquito.core.security.dto.api.LoginPagosMasivosResponse;
import com.banquito.core.security.dto.api.LoginRequest;
import com.banquito.core.security.dto.api.LoginResponse;
import com.banquito.core.security.service.AuthService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/core/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login exitoso", service.login(request));
    }

    @PostMapping("/pagos-masivos/login")
    public ApiResponse<LoginPagosMasivosResponse> loginPagosMasivos(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login de pagos masivos exitoso", service.loginPagosMasivos(request));
    }
}
