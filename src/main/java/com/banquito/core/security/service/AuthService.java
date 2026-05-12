package com.banquito.core.security.service;

import com.banquito.core.security.dto.CredencialWebResponse;
import com.banquito.core.security.dto.UsuarioCoreResponse;

public interface AuthService {
    
    CredencialWebResponse buscarCredencialWeb(String usuario);
    
    UsuarioCoreResponse buscarUsuarioCore(String usuario);
    
}
