package com.banquito.core.security.service.impl;

import com.banquito.core.security.dto.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.mapper.UsuarioCoreMapper;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioCoreServiceImpl implements UsuarioCoreService {
    private final UsuarioCoreRepository repository;
    
    @Override
    public UsuarioCoreResponse obtenerPorUsername(String username) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + username));
        return UsuarioCoreMapper.toResponse(usuario);
    }
    
    @Override
    public boolean validarRolYEstado(String username, RolUsuarioCoreEnum rolRequerido, EstadoUsuarioCoreEnum estadoRequerido) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + username));
        boolean rolValido = usuario.getRol() == rolRequerido;
        boolean estadoValido = usuario.getEstado() == estadoRequerido;
        boolean esValido = rolValido && estadoValido;
        return esValido;
    }
}
