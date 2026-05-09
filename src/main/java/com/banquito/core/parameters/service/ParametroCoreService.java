package com.banquito.core.parameters.service;

import com.banquito.core.parameters.dto.response.ParametroCoreResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.repository.ParametroCoreRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroCoreService {
    private final ParametroCoreRepository repository;
    public List<ParametroCoreResponse> listar() { return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); }
    public ParametroCoreResponse obtener(String codigo) { return repository.findById(codigo).map(ParametroMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Parametro no encontrado: " + codigo)); }
}
