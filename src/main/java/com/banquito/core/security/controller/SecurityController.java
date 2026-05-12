package com.banquito.core.security.controller;

import com.banquito.core.security.dto.response.CredencialWebResponse;
import com.banquito.core.security.dto.response.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.service.CredencialWebService;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
@Slf4j
public class SecurityController {
    private final UsuarioCoreService usuarioCoreService;
    private final CredencialWebService credencialWebService;
    
    @GetMapping("/usuarios-core/{username}")
    public ApiResponse<?> obtenerUsuarioCore(@PathVariable String username) {
        log.info("Obteniendo usuario core: {}", username);
        return ApiResponse.ok("Usuario core obtenido", usuarioCoreService.obtenerPorUsername(username));
    }
    
    @GetMapping("/credenciales-web/{username}")
    public ApiResponse<?> obtenerCredencialWeb(@PathVariable String username) {
        log.info("Obteniendo credencial web: {}", username);
        return ApiResponse.ok("Credencial web obtenida", credencialWebService.obtenerPorUsername(username));
    }
    
    @GetMapping("/usuarios-core/{username}/validacion")
    public ApiResponse<?> validarUsuarioCore(
            @PathVariable String username,
            @RequestParam RolUsuarioCoreEnum rol,
            @RequestParam EstadoUsuarioCoreEnum estado) {
        log.info("Validando usuario core: {} - rol: {}, estado: {}", username, rol, estado);
        boolean esValido = usuarioCoreService.validarRolYEstado(username, rol, estado);
        return ApiResponse.ok("Validación completada", esValido);
    }
    
    @GetMapping("/credenciales-web/{username}/validacion")
    public ApiResponse<?> validarCredencialWeb(
            @PathVariable String username,
            @RequestParam EstadoCredencialWebEnum estado) {
        log.info("Validando credencial web: {} - estado: {}", username, estado);
        boolean esValido = credencialWebService.validarEstado(username, estado);
        return ApiResponse.ok("Validación completada", esValido);
    }
}
