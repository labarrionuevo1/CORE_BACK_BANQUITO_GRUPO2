package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/core/feriados")
@RequiredArgsConstructor
public class FeriadoController {
    private final FeriadoService service;
    @GetMapping public ApiResponse<?> listar() { return ApiResponse.ok("Feriados obtenidos", service.listar()); }
    @GetMapping("/{fecha}") public ApiResponse<?> obtener(@PathVariable LocalDate fecha) { return ApiResponse.ok("Feriado obtenido", service.obtener(fecha)); }
}
