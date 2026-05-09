package com.banquito.core.institutional.service;

import com.banquito.core.institutional.dto.response.CuentaInstitucionalResponse;
import com.banquito.core.institutional.mapper.CuentaInstitucionalMapper;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaInstitucionalService {
    private final CuentaInstitucionalRepository repository;
    public List<CuentaInstitucionalResponse> listar() { return repository.findAll().stream().map(CuentaInstitucionalMapper::toResponse).toList(); }
    public CuentaInstitucionalResponse porCodigo(String codigo) { return repository.findByCodigo(codigo).map(CuentaInstitucionalMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada: " + codigo)); }
}
