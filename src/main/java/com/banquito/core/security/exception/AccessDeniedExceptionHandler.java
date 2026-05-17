package com.banquito.core.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String mensaje = escapeJson(getCustomMessage(accessDeniedException));
        String detalle = escapeJson(accessDeniedException.getMessage());

        String body = String.format(
            "{\"estado\":\"ERROR\",\"codigo\":\"ACCESO_DENEGADO\",\"mensaje\":\"%s\",\"detalle\":\"%s\"}",
            mensaje, detalle
        );

        response.getWriter().write(body);
    }

    private String getCustomMessage(AccessDeniedException ex) {
        if (ex.getMessage() != null) {
            String message = ex.getMessage();
            if (message.contains("ADMIN_CORE") && message.contains("TRANSACCION_CUENTA")) {
                return "VIOLACIÓN DE SEGREGACIÓN DE FUNCIONES: El rol ADMIN_CORE no tiene permiso para ejecutar transacciones financieras. Esta acción está bloqueada por política de seguridad.";
            }
            if (message.contains("AUDITOR") && (message.contains("INSERT") || message.contains("UPDATE") || message.contains("DELETE"))) {
                return "VIOLACIÓN DE SEGREGACIÓN DE FUNCIONES: El rol AUDITOR es de solo lectura. No tiene permiso para modificar datos en el sistema.";
            }
            if (message.contains("SUCURSAL_ID") || message.contains("sucursal")) {
                return "VIOLACIÓN DE ÁMBITO: No tiene permiso para operar en sucursales diferentes a la asignada.";
            }
            if (message.contains("subtipo") || message.contains("SUBTIPO")) {
                return "VIOLACIÓN DE PERMISOS: El rol no tiene autorización para ejecutar este tipo de transacción.";
            }
        }
        return "Acceso denegado: No tiene los permisos necesarios para realizar esta acción.";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
