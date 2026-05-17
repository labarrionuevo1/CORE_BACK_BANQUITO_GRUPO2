package com.banquito.core.security.service;

import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.shared.exception.AccessDeniedException;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class TransaccionSecurityService {

    private static final BigDecimal LIMITE_MONTO_CAJERO = new BigDecimal("5000.00");

    private static final Set<String> SUBTIPOS_CAJERO = Set.of(
            "DEP_EFECTIVO",
            "DEPOSITO_VENTANILLA",
            "RET_EFECTIVO"
    );

    private static final Set<String> SUBTIPOS_SISTEMA = Set.of(
            "ABONO_NOMINA",
            "PAGO_MASIVO",
            "COBRO_COMISION"
    );

    public boolean puedeCrearTransaccion(Authentication authentication, TransferenciaRequest request) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UsuarioCore)) {
            throw new AccessDeniedException("Usuario no autenticado correctamente");
        }

        UsuarioCore usuario = (UsuarioCore) principal;
        RolUsuarioCoreEnum rol = usuario.getRol();
        String codigoSubtipo = request.codigoSubtipo();

        switch (rol) {
            case CAJERO:
                validarSubtipoCajero(codigoSubtipo);
                validarMontoCajero(request.monto(), request.tokenAprobacion());
                break;

            case SISTEMA:
                validarSubtipoSistema(codigoSubtipo);
                break;

            case SUPERVISOR_AGENCIA:
                validarSubtipoCajero(codigoSubtipo);
                break;

            case ADMIN_CORE:
                throw new AccessDeniedException(
                    "VIOLACIÓN DE SEGREGACIÓN DE FUNCIONES: El rol ADMIN_CORE no tiene permiso para ejecutar transacciones financieras. " +
                    "Esta acción está bloqueada por política de seguridad."
                );

            case AUDITOR:
                throw new AccessDeniedException(
                    "VIOLACIÓN DE SEGREGACIÓN DE FUNCIONES: El rol AUDITOR es de solo lectura. " +
                    "No tiene permiso para ejecutar transacciones financieras."
                );

            default:
                throw new AccessDeniedException(
                    "El rol " + rol.getValue() + " no tiene permiso para ejecutar transacciones."
                );
        }

        return true;
    }

    private void validarSubtipoCajero(String codigoSubtipo) {
        if (!SUBTIPOS_CAJERO.contains(codigoSubtipo)) {
            throw new AccessDeniedException(
                String.format("VIOLACIÓN DE PERMISOS: El rol CAJERO no tiene autorización para ejecutar transacciones del subtipo '%s'. " +
                        "Subtipos permitidos: %s", codigoSubtipo, SUBTIPOS_CAJERO)
            );
        }
    }

    private void validarSubtipoSistema(String codigoSubtipo) {
        if (!SUBTIPOS_SISTEMA.contains(codigoSubtipo)) {
            throw new AccessDeniedException(
                String.format("VIOLACIÓN DE PERMISOS: El rol SISTEMA no tiene autorización para ejecutar transacciones del subtipo '%s'. " +
                        "Subtipos permitidos: %s", codigoSubtipo, SUBTIPOS_SISTEMA)
            );
        }
    }

    private void validarMontoCajero(BigDecimal monto, String tokenAprobacion) {
        if (monto.compareTo(LIMITE_MONTO_CAJERO) > 0) {
            if (tokenAprobacion == null || tokenAprobacion.trim().isEmpty()) {
                throw new AccessDeniedException(
                    String.format("TRANSACCIÓN BLOQUEADA: El monto $%.2f excede el límite permitido de $%.2f para CAJERO. " +
                            "Se requiere token de aprobación del SUPERVISOR_AGENCIA.", monto, LIMITE_MONTO_CAJERO)
                );
            }
            if (!validarTokenAprobacion(tokenAprobacion)) {
                throw new AccessDeniedException(
                    "TOKEN DE APROBACIÓN INVÁLIDO: El token proporcionado no es válido o ha expirado."
                );
            }
        }
    }

    private boolean validarTokenAprobacion(String token) {
        return token != null && token.length() >= 16;
    }

    public boolean puedeAprobarTransaccionBloqueada(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UsuarioCore)) {
            return false;
        }

        UsuarioCore usuario = (UsuarioCore) principal;
        return usuario.getRol() == RolUsuarioCoreEnum.SUPERVISOR_AGENCIA;
    }
}
