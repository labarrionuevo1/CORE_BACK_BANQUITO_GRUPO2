package com.banquito.core.security.service.impl;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.security.service.CredencialWebService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CredencialWebServiceImpl implements CredencialWebService {

    private final CredencialWebRepository repository;

    @Override
    @Transactional(readOnly = true)
    public CredencialWebResponse obtenerPorUsername(String username) {
        CredencialWeb credencial = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credencial web no encontrada: " + username
                ));

        return SeguridadMapper.toResponse(credencial);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarEstado(String username, EstadoCredencialWebEnum estadoRequerido) {
        CredencialWeb credencial = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credencial web no encontrada: " + username
                ));

        return credencial.getEstado() == estadoRequerido;
    }
}