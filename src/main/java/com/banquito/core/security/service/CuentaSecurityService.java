package com.banquito.core.security.service;

import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.shared.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CuentaSecurityService {

    private final CuentaRepository cuentaRepository;

    public CuentaSecurityService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public boolean puedeCambiarEstadoLocal(Authentication authentication, Integer cuentaId) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UsuarioCore)) {
            throw new AccessDeniedException("Usuario no autenticado correctamente");
        }

        UsuarioCore usuario = (UsuarioCore) principal;
        RolUsuarioCoreEnum rol = usuario.getRol();

        if (rol != RolUsuarioCoreEnum.SUPERVISOR_AGENCIA) {
            return false;
        }

        Integer usuarioSucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
        if (usuarioSucursalId == null) {
            throw new AccessDeniedException(
                "El rol SUPERVISOR_AGENCIA debe tener una sucursal asignada para cambiar estados de cuentas."
            );
        }

        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new AccessDeniedException("Cuenta no encontrada: " + cuentaId));

        Integer cuentaSucursalId = cuenta.getSucursal() != null ? cuenta.getSucursal().getId() : null;
        if (cuentaSucursalId == null || !cuentaSucursalId.equals(usuarioSucursalId)) {
            throw new AccessDeniedException(
                String.format("VIOLACIÓN DE ÁMBITO: El rol SUPERVISOR_AGENCIA no tiene permiso para cambiar el estado de cuentas de otra sucursal. " +
                        "Sucursal del usuario: %d, Sucursal de la cuenta: %d", usuarioSucursalId, cuentaSucursalId)
            );
        }

        return true;
    }
}
