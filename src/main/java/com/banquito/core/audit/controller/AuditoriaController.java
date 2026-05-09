package com.banquito.core.audit.controller;

import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {
    private final AuditoriaService service;
    @GetMapping public ApiResponse<?> listar() { return ApiResponse.ok("Eventos de auditoria obtenidos", service.listar()); }
}
