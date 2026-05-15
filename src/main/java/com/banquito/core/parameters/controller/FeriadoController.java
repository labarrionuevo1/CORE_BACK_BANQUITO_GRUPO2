package com.banquito.core.parameters.controller;

import com.banquito.core.parameters.dto.api.DiaHabilResponse;
import com.banquito.core.parameters.dto.api.FeriadoRequest;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
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

    @PostMapping
    public ApiResponse<?> crear(@Valid @RequestBody FeriadoRequest request) {
        return ApiResponse.ok("Feriado creado", service.crear(request));
    }

    @PutMapping("/{fecha}")
    public ApiResponse<?> actualizar(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha, @Valid @RequestBody FeriadoRequest request) {
        return ApiResponse.ok("Feriado actualizado", service.actualizar(fecha, request));
    }

    @DeleteMapping("/{fecha}")
    public ApiResponse<?> eliminar(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        service.eliminar(fecha);
        return ApiResponse.ok("Feriado eliminado", null);
    }
}
