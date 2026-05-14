package com.banquito.core.security.service.impl;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.security.service.AuthService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CredencialWebRepository credencialWebRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;
    
    @Override
    public CredencialWebResponse buscarCredencialWeb(String usuario) {
        return credencialWebRepository.findByUsuario(usuario)
                .map(SeguridadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial web no encontrada: " + usuario));
    }
    
    @Override
    public UsuarioCoreResponse buscarUsuarioCore(String usuario) {
        return usuarioCoreRepository.findByUsuario(usuario)
                .map(SeguridadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario Core no encontrado: " + usuario));
    }
}
