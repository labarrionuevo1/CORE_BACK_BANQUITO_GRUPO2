package com.banquito.core.security.service.impl;

import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioCoreServiceImpl implements UsuarioCoreService {

    private final UsuarioCoreRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UsuarioCoreResponse obtenerPorUsername(String username) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + username
                ));

        return SeguridadMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarRolYEstado(
            String username,
            RolUsuarioCoreEnum rolRequerido,
            EstadoUsuarioCoreEnum estadoRequerido
    ) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + username
                ));

        boolean rolValido = usuario.getRol() == rolRequerido;
        boolean estadoValido = usuario.getEstado() == estadoRequerido;

        return rolValido && estadoValido;
    }
}