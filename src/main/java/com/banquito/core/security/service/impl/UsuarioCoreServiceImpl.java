package com.banquito.core.security.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.branches.service.SucursalService;
import com.banquito.core.security.dto.api.UsuarioCoreRequest;
import com.banquito.core.security.dto.api.UsuarioCoreResponse;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.mapper.SeguridadMapper;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.security.service.UsuarioCoreService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioCoreServiceImpl implements UsuarioCoreService {

    private static final String MODULO_SECURITY = "SECURITY";

    private final UsuarioCoreRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final SucursalService sucursalService;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCoreResponse> listar() {
        return repository.findAll().stream()
                .map(SeguridadMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioCoreResponse obtener(Integer id) {
        return repository.findById(id)
                .map(SeguridadMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioCoreResponse obtenerPorUsername(String username) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + username
                ));

        return SeguridadMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarRolYEstado(
            String username,
            RolUsuarioCoreEnum rolRequerido,
            EstadoUsuarioCoreEnum estadoRequerido
    ) {
        UsuarioCore usuario = repository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + username
                ));

        boolean rolValido = usuario.getRol() == rolRequerido;
        boolean estadoValido = usuario.getEstado() == estadoRequerido;

        return rolValido && estadoValido;
    }

    @Override
    @Transactional
    public UsuarioCoreResponse crear(UsuarioCoreRequest request) {
        repository.findByUsuario(request.usuario())
                .ifPresent(u -> {
                    throw new BusinessException("Ya existe un usuario con el nombre: " + request.usuario());
                });

        UsuarioCore usuario = SeguridadMapper.toEntity(request);
        usuario.setPasswordHash(passwordEncoder.encode(request.contrasena()));

        if (request.sucursalId() != null) {
            Sucursal sucursal = sucursalService.obtenerEntidad(request.sucursalId());
            usuario.setSucursal(sucursal);
        }

        UsuarioCore usuarioGuardado = repository.save(usuario);

        auditoriaService.registrarEvento(
                MODULO_SECURITY,
                "CREAR_USUARIO_CORE",
                "USUARIO_CORE",
                usuarioGuardado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"usuario\":\"" + usuarioGuardado.getUsuario() + "\"}"
        );

        return SeguridadMapper.toResponse(usuarioGuardado);
    }

    @Override
    @Transactional
    public UsuarioCoreResponse actualizar(Integer id, UsuarioCoreRequest request) {
        UsuarioCore usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + id));

        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setRol(RolUsuarioCoreEnum.valueOf(request.rol()));

        if (request.sucursalId() != null) {
            Sucursal sucursal = sucursalService.obtenerEntidad(request.sucursalId());
            usuario.setSucursal(sucursal);
        } else {
            usuario.setSucursal(null);
        }

        if (request.contrasena() != null && !request.contrasena().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.contrasena()));
        }

        UsuarioCore usuarioActualizado = repository.save(usuario);

        auditoriaService.registrarEvento(
                MODULO_SECURITY,
                "ACTUALIZAR_USUARIO_CORE",
                "USUARIO_CORE",
                usuarioActualizado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Usuario core actualizado"
        );

        return SeguridadMapper.toResponse(usuarioActualizado);
    }

    @Override
    @Transactional
    public UsuarioCoreResponse cambiarEstado(Integer id, EstadoUsuarioCoreEnum nuevoEstado) {
        UsuarioCore usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + id));
        
        EstadoUsuarioCoreEnum estadoAnterior = usuario.getEstado();
        usuario.setEstado(nuevoEstado);
        UsuarioCore usuarioGuardado = repository.save(usuario);

        auditoriaService.registrarEvento(
                MODULO_SECURITY,
                "CAMBIAR_ESTADO_USUARIO_CORE",
                "USUARIO_CORE",
                usuarioGuardado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"estadoAnterior\":\"" + estadoAnterior + "\",\"estadoNuevo\":\"" + nuevoEstado + "\"}"
        );

        return SeguridadMapper.toResponse(usuarioGuardado);
    }
}
