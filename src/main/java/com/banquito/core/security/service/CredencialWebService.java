package com.banquito.core.security.service;

import com.banquito.core.security.dto.response.CredencialWebResponse;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.mapper.CredencialWebMapper;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CredencialWebService {
    private final CredencialWebRepository repository;
    
    public CredencialWebResponse obtenerPorUsername(String username) {
        log.info("Buscando credencial web por username: {}", username);
        CredencialWeb credencial = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial web no encontrada: " + username));
        
        return CredencialWebMapper.toResponse(credencial);
    }
    
    public boolean validarEstado(String username, EstadoCredencialWebEnum estadoRequerido) {
        log.info("Validando estado de credencial web: {} - estado requerido: {}", username, estadoRequerido);
        
        CredencialWeb credencial = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial web no encontrada: " + username));
        
        boolean estadoValido = credencial.getEstado() == estadoRequerido;
        
        log.info("Credencial {} - estado actual: {}, válido: {}", 
                username, credencial.getEstado(), estadoValido);
        
        return estadoValido;
    }
}
