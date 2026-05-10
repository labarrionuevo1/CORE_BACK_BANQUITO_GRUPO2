package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.response.ParametroCoreResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.ParametroCoreRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametroCoreService {
    private final ParametroCoreRepository repository;
    
    public List<ParametroCoreResponse> listar() { 
        log.info("Listando todos los parámetros");
        return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); 
    }
    
    public List<ParametroCoreResponse> listarActivos() { 
        log.info("Listando parámetros activos");
        return repository.findAll().stream()
                .filter(param -> param.getValorTexto() != null && !param.getValorTexto().trim().isEmpty())
                .map(ParametroMapper::toResponse).toList(); 
    }
    
    public ParametroCoreResponse obtener(String codigo) { 
        log.info("Obteniendo parámetro por código: {}", codigo);
        return repository.findById(codigo).map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Parametro no encontrado: " + codigo)); 
    }
}
