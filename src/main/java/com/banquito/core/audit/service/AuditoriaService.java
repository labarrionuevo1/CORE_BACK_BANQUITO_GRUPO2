package com.banquito.core.audit.service;

import com.banquito.core.audit.dto.response.AuditoriaEventoResponse;
import com.banquito.core.audit.mapper.AuditoriaMapper;
import com.banquito.core.audit.repository.AuditoriaEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaEventoRepository repository;
    public List<AuditoriaEventoResponse> listar() { return repository.findAll().stream().map(AuditoriaMapper::toResponse).toList(); }
}
