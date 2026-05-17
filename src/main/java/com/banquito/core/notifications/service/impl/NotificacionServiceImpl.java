package com.banquito.core.notifications.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.notifications.dto.internal.EnviarCorreoRequest;
import com.banquito.core.notifications.service.NotificacionService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private static final String MODULO_NOTIFICATIONS = "NOTIFICATIONS";
    private static final String ENTIDAD_NOTIFICACION = "CORREO_SMTP";

    private final JavaMailSender mailSender;
    private final AuditoriaService auditoriaService;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@banquito.local}")
    private String remitente;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarCorreo(
            EnviarCorreoRequest request,
            String accionOrigen,
            String entidadOrigen,
            String entidadIdOrigen,
            CanalOrigenEnum canalOrigen
    ) {
        if (!mailEnabled) {
            registrarAuditoria(
                    "CORREO_OMITIDO",
                    request,
                    accionOrigen,
                    entidadOrigen,
                    entidadIdOrigen,
                    canalOrigen,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "Servicio de correo deshabilitado por configuracion"
            );
            return;
        }

        if (request == null
                || esTextoVacio(request.destinatario())
                || esTextoVacio(request.asunto())
                || esTextoVacio(request.contenido())) {
            registrarAuditoria(
                    "CORREO_OMITIDO",
                    request,
                    accionOrigen,
                    entidadOrigen,
                    entidadIdOrigen,
                    canalOrigen,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "Datos requeridos de correo incompletos"
            );
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(request.destinatario());
            helper.setSubject(request.asunto());
            helper.setText(request.contenido(), true);

            mailSender.send(mensaje);

            registrarAuditoria(
                    "CORREO_ENVIADO",
                    request,
                    accionOrigen,
                    entidadOrigen,
                    entidadIdOrigen,
                    canalOrigen,
                    ResultadoAuditoriaEnum.EXITOSO,
                    null
            );
        } catch (MailException | MessagingException ex) {
            registrarAuditoria(
                    "CORREO_FALLIDO",
                    request,
                    accionOrigen,
                    entidadOrigen,
                    entidadIdOrigen,
                    canalOrigen,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    ex.getMessage()
            );
        }
    }

    private void registrarAuditoria(
            String accion,
            EnviarCorreoRequest request,
            String accionOrigen,
            String entidadOrigen,
            String entidadIdOrigen,
            CanalOrigenEnum canalOrigen,
            ResultadoAuditoriaEnum resultado,
            String error
    ) {
        try {
            auditoriaService.registrarEventoNuevaTransaccion(
                    MODULO_NOTIFICATIONS,
                    accion,
                    ENTIDAD_NOTIFICACION,
                    entidadIdOrigen != null ? entidadIdOrigen : "SIN_ENTIDAD",
                    resultado,
                    canalOrigen != null ? canalOrigen : CanalOrigenEnum.CORE,
                    construirDetalleJson(request, accionOrigen, entidadOrigen, entidadIdOrigen, error)
            );
        } catch (RuntimeException ignored) {
            // La notificacion es un efecto secundario. No debe revertir ni bloquear la operacion bancaria principal.
        }
    }

    private String construirDetalleJson(
            EnviarCorreoRequest request,
            String accionOrigen,
            String entidadOrigen,
            String entidadIdOrigen,
            String error
    ) {
        String destinatario = request != null ? request.destinatario() : null;
        String asunto = request != null ? request.asunto() : null;

        return "{\"destinatario\":\"" + sanitizarJson(destinatario) +
                "\",\"asunto\":\"" + sanitizarJson(asunto) +
                "\",\"accionOrigen\":\"" + sanitizarJson(accionOrigen) +
                "\",\"entidadOrigen\":\"" + sanitizarJson(entidadOrigen) +
                "\",\"entidadIdOrigen\":\"" + sanitizarJson(entidadIdOrigen) +
                "\",\"error\":\"" + sanitizarJson(error) + "\"}";
    }

    private boolean esTextoVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String sanitizarJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}