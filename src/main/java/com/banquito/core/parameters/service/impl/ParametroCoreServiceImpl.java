package com.banquito.core.parameters.service.impl;

import com.banquito.core.parameters.dto.api.ParametroCoreResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.ParametroCoreRepository;
import com.banquito.core.parameters.service.ParametroCoreService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroCoreServiceImpl implements ParametroCoreService {
    private final ParametroCoreRepository repository;
    
    @Override
    public List<ParametroCoreResponse> listar() {
        return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); 
    }
    
    @Override
    public List<ParametroCoreResponse> listarActivos() {
        return repository.findAll().stream()
                .filter(param -> param.getValorTexto() != null && !param.getValorTexto().trim().isEmpty())
                .map(ParametroMapper::toResponse).toList(); 
    }
    
    @Override
    public ParametroCoreResponse obtener(String codigo) {
        return repository.findById(codigo).map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Parametro no encontrado: " + codigo)); 
    }
}
