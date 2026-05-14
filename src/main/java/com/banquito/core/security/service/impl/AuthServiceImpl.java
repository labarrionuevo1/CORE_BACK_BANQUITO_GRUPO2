package com.banquito.core.security.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.security.dto.api.CredencialWebResponse;
import com.banquito.core.security.dto.api.LoginRequest;
import com.banquito.core.security.dto.api.LoginResponse;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.security.service.AuthService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String MODULO_SEGURIDAD = "SEGURIDAD";
    private static final String ACCION_LOGIN = "LOGIN";
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contrasena incorrectos";

    private final CredencialWebRepository credencialWebRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public CredencialWebResponse buscarCredencialWeb(String usuario) {
        return credencialWebRepository.findByUsuario(usuario)
                .map(SeguridadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial web no encontrada: " + usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioCoreResponse buscarUsuarioCore(String usuario) {
        return usuarioCoreRepository.findByUsuario(usuario)
                .map(SeguridadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario Core no encontrado: " + usuario));
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String usuario = request.usuario().trim();

        var usuarioCoreOptional = usuarioCoreRepository.findByUsuario(usuario);
        if (usuarioCoreOptional.isPresent()) {
            return loginUsuarioCore(usuarioCoreOptional.get(), request.contrasena());
        }

        var credencialWebOptional = credencialWebRepository.findByUsuario(usuario);
        if (credencialWebOptional.isPresent()) {
            return loginCredencialWeb(credencialWebOptional.get(), request.contrasena());
        }

        registrarLoginFallido(usuario, "USUARIO_NO_EXISTE");
        throw new ValidationException(MENSAJE_CREDENCIALES_INVALIDAS);
    }

    private LoginResponse loginUsuarioCore(UsuarioCore usuarioCore, String contrasena) {
        if (usuarioCore.getEstado() != EstadoUsuarioCoreEnum.ACTIVO) {
            registrarEventoUsuarioCore(usuarioCore, ResultadoAuditoriaEnum.RECHAZADO, "USUARIO_NO_ACTIVO");
            throw new ValidationException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        if (!passwordEncoder.matches(contrasena, usuarioCore.getPasswordHash())) {
            registrarEventoUsuarioCore(usuarioCore, ResultadoAuditoriaEnum.RECHAZADO, "PASSWORD_INVALIDO");
            throw new ValidationException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        LocalDateTime ahora = LocalDateTime.now();
        usuarioCore.setUltimoLogin(ahora);
        usuarioCoreRepository.save(usuarioCore);

        registrarEventoUsuarioCore(usuarioCore, ResultadoAuditoriaEnum.EXITOSO, "LOGIN_EXITOSO");

        return new LoginResponse(
                "INTERNO",
                usuarioCore.getId(),
                null,
                null,
                usuarioCore.getUsuario(),
                usuarioCore.getNombreCompleto(),
                usuarioCore.getRol().name(),
                usuarioCore.getEstado().name(),
                ahora
        );
    }

    private LoginResponse loginCredencialWeb(CredencialWeb credencialWeb, String contrasena) {
        if (credencialWeb.getEstado() != EstadoCredencialWebEnum.ACTIVO) {
            registrarEventoCredencialWeb(credencialWeb, ResultadoAuditoriaEnum.RECHAZADO, "CREDENCIAL_NO_ACTIVA");
            throw new ValidationException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        if (!passwordEncoder.matches(contrasena, credencialWeb.getPasswordHash())) {
            registrarEventoCredencialWeb(credencialWeb, ResultadoAuditoriaEnum.RECHAZADO, "PASSWORD_INVALIDO");
            throw new ValidationException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        LocalDateTime ahora = LocalDateTime.now();
        credencialWeb.setUltimoLogin(ahora);
        credencialWebRepository.save(credencialWeb);

        registrarEventoCredencialWeb(credencialWeb, ResultadoAuditoriaEnum.EXITOSO, "LOGIN_EXITOSO");

        String nombre = credencialWeb.getCliente().getRazonSocial() != null
                ? credencialWeb.getCliente().getRazonSocial()
                : (credencialWeb.getCliente().getNombres() + " " + credencialWeb.getCliente().getApellidos());

        return new LoginResponse(
                "EMPRESA",
                null,
                credencialWeb.getId(),
                credencialWeb.getCliente().getId(),
                credencialWeb.getUsuario(),
                nombre,
                null,
                credencialWeb.getEstado().name(),
                ahora
        );
    }

    private void registrarEventoUsuarioCore(
            UsuarioCore usuarioCore,
            ResultadoAuditoriaEnum resultado,
            String motivo
    ) {
        auditoriaService.registrarEventoNuevaTransaccion(
                MODULO_SEGURIDAD,
                ACCION_LOGIN,
                "USUARIO_CORE",
                usuarioCore.getId().toString(),
                resultado,
                CanalOrigenEnum.CORE,
                "{\"usuario\":\"" + sanitizarJson(usuarioCore.getUsuario()) + "\",\"motivo\":\"" + motivo + "\"}"
        );
    }

    private void registrarEventoCredencialWeb(
            CredencialWeb credencialWeb,
            ResultadoAuditoriaEnum resultado,
            String motivo
    ) {
        auditoriaService.registrarEventoNuevaTransaccion(
                MODULO_SEGURIDAD,
                ACCION_LOGIN,
                "CREDENCIAL_WEB",
                credencialWeb.getId().toString(),
                resultado,
                CanalOrigenEnum.WEB,
                "{\"usuario\":\"" + sanitizarJson(credencialWeb.getUsuario()) + "\",\"motivo\":\"" + motivo + "\"}"
        );
    }

    private void registrarLoginFallido(String usuario, String motivo) {
        auditoriaService.registrarEventoNuevaTransaccion(
                MODULO_SEGURIDAD,
                ACCION_LOGIN,
                "AUTENTICACION",
                usuario,
                ResultadoAuditoriaEnum.RECHAZADO,
                CanalOrigenEnum.WEB,
                "{\"usuario\":\"" + sanitizarJson(usuario) + "\",\"motivo\":\"" + motivo + "\"}"
        );
    }

    private String sanitizarJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}