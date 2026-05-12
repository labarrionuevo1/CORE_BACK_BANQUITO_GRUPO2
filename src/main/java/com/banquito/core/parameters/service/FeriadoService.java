package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.FeriadoResponse;

import java.time.LocalDate;
import java.util.List;

public interface FeriadoService {
    
    List<FeriadoResponse> listar();
    
    FeriadoResponse obtener(LocalDate fecha);
    
    LocalDate calcularSiguienteDiaHabil(LocalDate fecha);
    
}
