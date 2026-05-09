package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.response.FeriadoResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.FeriadoRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriadoService {
    private final FeriadoRepository repository;
    public List<FeriadoResponse> listar() { return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); }
    public FeriadoResponse obtener(LocalDate fecha) { return repository.findById(fecha).map(ParametroMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Feriado no encontrado: " + fecha)); }
}
