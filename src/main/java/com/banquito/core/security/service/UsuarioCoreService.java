package com.banquito.core.security.service;

import com.banquito.core.security.dto.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.mapper.UsuarioCoreMapper;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioCoreService {
    private final UsuarioCoreRepository repository;
    
    public UsuarioCoreResponse obtenerPorUsername(String username) {
        log.info("Buscando usuario core por username: {}", username);
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + username));
        
        return UsuarioCoreMapper.toResponse(usuario);
    }
    
    public boolean validarRolYEstado(String username, RolUsuarioCoreEnum rolRequerido, EstadoUsuarioCoreEnum estadoRequerido) {
        log.info("Validando rol y estado del usuario: {} - rol: {}, estado: {}", 
                username, rolRequerido, estadoRequerido);
        
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + username));
        
        boolean rolValido = usuario.getRol() == rolRequerido;
        boolean estadoValido = usuario.getEstado() == estadoRequerido;
        boolean esValido = rolValido && estadoValido;
        
        log.info("Usuario {} - rol válido: {}, estado válido: {}, válido: {}", 
                username, rolValido, estadoValido, esValido);
        
        return esValido;
    }
}
