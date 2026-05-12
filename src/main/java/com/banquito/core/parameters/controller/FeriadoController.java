package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/core/feriados")
@RequiredArgsConstructor
@Slf4j
public class FeriadoController {
    private final FeriadoService service;
    
    @GetMapping
    public ApiResponse<?> listar() { 
        log.info("Listando todos los feriados");
        return ApiResponse.ok("Feriados obtenidos", service.listar()); 
    }
    
    @GetMapping("/{fecha}")
    public ApiResponse<?> obtener(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) { 
        log.info("Buscando feriado por fecha: {}", fecha);
        return ApiResponse.ok("Feriado obtenido", service.obtener(fecha)); 
    }
    
    @GetMapping("/siguiente-dia-habil")
    public ApiResponse<?> calcularSiguienteDiaHabil(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        log.info("Calculando siguiente día hábil desde: {}", fecha);
        LocalDate siguienteDiaHabil = service.calcularSiguienteDiaHabil(fecha);
        return ApiResponse.ok("Siguiente día hábil calculado", siguienteDiaHabil);
    }
}
