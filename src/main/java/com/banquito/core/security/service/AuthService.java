package com.banquito.core.security.service;

import com.banquito.core.security.dto.CredencialWebResponse;
import com.banquito.core.security.dto.UsuarioCoreResponse;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredencialWebRepository credencialWebRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;
    public CredencialWebResponse buscarCredencialWeb(String usuario) {
        return credencialWebRepository.findByUsuario(usuario).map(SeguridadMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Credencial web no encontrada"));
    }
    public UsuarioCoreResponse buscarUsuarioCore(String usuario) {
        return usuarioCoreRepository.findByUsuario(usuario).map(SeguridadMapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("Usuario Core no encontrado"));
    }
}
