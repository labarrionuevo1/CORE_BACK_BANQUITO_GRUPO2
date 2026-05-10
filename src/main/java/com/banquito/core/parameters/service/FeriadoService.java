package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.response.FeriadoResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.FeriadoRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeriadoService {
    private final FeriadoRepository repository;
    
    public List<FeriadoResponse> listar() { 
        log.info("Listando todos los feriados");
        return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); 
    }
    
    public FeriadoResponse obtener(LocalDate fecha) { 
        log.info("Buscando feriado por fecha: {}", fecha);
        return repository.findById(fecha).map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado no encontrado: " + fecha)); 
    }
    
    public LocalDate calcularSiguienteDiaHabil(LocalDate fecha) {
        log.info("Calculando siguiente día hábil desde: {}", fecha);
        
        LocalDate siguienteDia = fecha.plusDays(1);
        
        while (!esDiaHabil(siguienteDia)) {
            siguienteDia = siguienteDia.plusDays(1);
            log.debug("Saltando día no hábil: {}", siguienteDia.minusDays(1));
        }
        
        log.info("Siguiente día hábil encontrado: {}", siguienteDia);
        return siguienteDia;
    }
    
    private boolean esDiaHabil(LocalDate fecha) {
        // Verificar si es fin de semana
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        
        // Verificar si es feriado
        return !repository.existsById(fecha);
    }
}
