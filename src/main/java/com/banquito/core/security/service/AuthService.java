package com.banquito.core.security.service;

import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.dto.api.LoginRequest;
import com.banquito.core.security.dto.api.LoginResponse;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;

public interface AuthService {

    CredencialWebResponse buscarCredencialWeb(String usuario);

    UsuarioCoreResponse buscarUsuarioCore(String usuario);

    LoginResponse login(LoginRequest request);
}