package com.banquito.core.security.service;

import com.banquito.core.security.dto.api.*;

public interface AuthService {

    CredencialWebResponse buscarCredencialWeb(String usuario);

    UsuarioCoreResponse buscarUsuarioCore(String usuario);

    LoginResponse login(LoginRequest request);

    LoginPagosMasivosResponse loginPagosMasivos(LoginRequest request);
}
