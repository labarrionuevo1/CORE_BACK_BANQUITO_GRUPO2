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

    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Eventos de auditoria obtenidos", service.listar());
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    public ApiResponse<?> consultarPorEntidad(
            @PathVariable String entidad,
            @PathVariable String entidadId
    ) {
        return ApiResponse.ok(
                "Eventos de auditoria por entidad obtenidos",
                service.consultarPorEntidad(entidad, entidadId)
        );
    }

    @GetMapping("/modulo/{modulo}")
    public ApiResponse<?> consultarPorModulo(@PathVariable String modulo) {
        return ApiResponse.ok(
                "Eventos de auditoria por modulo obtenidos",
                service.consultarPorModulo(modulo)
        );
    }
}