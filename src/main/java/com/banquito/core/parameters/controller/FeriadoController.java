package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.dto.api.DiaHabilResponse;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/core/feriados")
@RequiredArgsConstructor
public class FeriadoController {
    private final FeriadoService service;
    
    @GetMapping
    public ApiResponse<?> listar() {
        return ApiResponse.ok("Feriados obtenidos", service.listar()); 
    }
    
    @GetMapping("/{fecha}")
    public ApiResponse<?> obtener(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        return ApiResponse.ok("Feriado obtenido", service.obtener(fecha)); 
    }
    
    @GetMapping("/siguiente-dia-habil")
    public ApiResponse<?> calcularSiguienteDiaHabil(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        LocalDate siguienteDiaHabil = service.calcularSiguienteDiaHabil(fecha);
        Integer diasCalculados = (int) java.time.temporal.ChronoUnit.DAYS.between(fecha, siguienteDiaHabil);
        String mensaje = String.format("Se calcularon %d días desde %s hasta %s", diasCalculados, fecha, siguienteDiaHabil);
        
        DiaHabilResponse response = ParametroMapper.toDiaHabilResponse(fecha, siguienteDiaHabil, diasCalculados, mensaje);
        return ApiResponse.ok("Siguiente día hábil calculado", response);
    }
}
