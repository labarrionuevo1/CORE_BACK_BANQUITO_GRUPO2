package com.banquito.core.parameters.service.impl;

import com.banquito.core.parameters.dto.FeriadoResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.FeriadoRepository;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriadoServiceImpl implements FeriadoService {
    private final FeriadoRepository repository;
    
    @Override
    public List<FeriadoResponse> listar() {
        return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); 
    }
    
    @Override
    public FeriadoResponse obtener(LocalDate fecha) {
        return repository.findById(fecha).map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado no encontrado: " + fecha)); 
    }
    
    @Override
    public LocalDate calcularSiguienteDiaHabil(LocalDate fecha) {
        LocalDate siguienteDia = fecha.plusDays(1);
        
        while (!esDiaHabil(siguienteDia)) {
            siguienteDia = siguienteDia.plusDays(1);
        }

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
