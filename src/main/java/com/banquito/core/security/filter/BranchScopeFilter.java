package com.banquito.core.security.filter;

import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.shared.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Set;

@Component
public class BranchScopeFilter implements HandlerInterceptor {

    private final CuentaRepository cuentaRepository;

    public BranchScopeFilter(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    private static final Set<RolUsuarioCoreEnum> BRANCH_RESTRICTED_ROLES = Set.of(
            RolUsuarioCoreEnum.CAJERO,
            RolUsuarioCoreEnum.SUPERVISOR_AGENCIA
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UsuarioCore)) {
            return true;
        }

        UsuarioCore usuario = (UsuarioCore) principal;
        RolUsuarioCoreEnum rol = usuario.getRol();

        if (!BRANCH_RESTRICTED_ROLES.contains(rol)) {
            return true;
        }

        Integer usuarioSucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
        if (usuarioSucursalId == null) {
            throw new AccessDeniedException(
                "El usuario " + rol.getValue() + " debe tener una sucursal asignada para realizar esta operación."
            );
        }

        validateBranchScope(request, usuarioSucursalId, rol);
        return true;
    }

    private void validateBranchScope(HttpServletRequest request, Integer usuarioSucursalId, RolUsuarioCoreEnum rol) {
        String path = request.getRequestURI();

        String numeroCuenta = extractNumeroCuenta(path);
        if (numeroCuenta != null) {
            Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                    .orElseThrow(() -> new AccessDeniedException("Cuenta no encontrada: " + numeroCuenta));
            
            Integer cuentaSucursalId = cuenta.getSucursal() != null ? cuenta.getSucursal().getId() : null;
            if (cuentaSucursalId == null || !cuentaSucursalId.equals(usuarioSucursalId)) {
                throw new AccessDeniedException(
                    String.format("VIOLACIÓN DE ÁMBITO: El rol %s no tiene permiso para operar en la sucursal de la cuenta %s. " +
                            "Sucursal del usuario: %d, Sucursal de la cuenta: %d",
                            rol.getValue(), numeroCuenta, usuarioSucursalId, cuentaSucursalId)
                );
            }
        }

        String clienteIdStr = extractClienteId(path);
        if (clienteIdStr != null) {
            try {
                Integer clienteId = Integer.parseInt(clienteIdStr);
                List<Cuenta> cuentasCliente = cuentaRepository.findByClienteId(clienteId);
                boolean tieneAcceso = cuentasCliente.isEmpty() || cuentasCliente.stream()
                        .anyMatch(c -> c.getSucursal() != null && c.getSucursal().getId().equals(usuarioSucursalId));
                if (!tieneAcceso) {
                    throw new AccessDeniedException(
                        String.format("VIOLACIÓN DE ÁMBITO: El rol %s no tiene permiso para operar con clientes de otra sucursal. " +
                                "Sucursal del usuario: %d", rol.getValue(), usuarioSucursalId)
                    );
                }
            } catch (NumberFormatException e) {
                // Cliente ID inválido, dejar pasar para que el validador de @PathVariable maneje el error
            }
        }
    }

    private String extractNumeroCuenta(String path) {
        if (path.matches(".*/cuentas/numero/[^/]+.*")) {
            String[] parts = path.split("/cuentas/numero/");
            if (parts.length > 1) {
                String remaining = parts[1].split("/")[0];
                return remaining;
            }
        }
        if (path.matches(".*/cuentas/[^/]+/saldo")) {
            String[] parts = path.split("/cuentas/");
            if (parts.length > 1) {
                String remaining = parts[1].split("/")[0];
                return remaining;
            }
        }
        return null;
    }

    private String extractClienteId(String path) {
        if (path.matches(".*/cuentas/cliente/\\d+.*")) {
            String[] parts = path.split("/cliente/");
            if (parts.length > 1) {
                String remaining = parts[1].split("/")[0];
                return remaining;
            }
        }
        return null;
    }
}
