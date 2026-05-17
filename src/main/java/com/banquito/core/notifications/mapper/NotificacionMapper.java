package com.banquito.core.notifications.mapper;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.BloqueoCuenta;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.notifications.dto.internal.EnviarCorreoRequest;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class NotificacionMapper {

    private static final String BANCO_NOMBRE = "Banco BanQuito";
    private static final String ASUNTO_BLOQUEO_CUENTA = "Notificacion de bloqueo de cuenta BanQuito";
    private static final String ASUNTO_CAMBIO_ESTADO_CUENTA = "Notificacion de cambio de estado de cuenta BanQuito";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private NotificacionMapper() {
    }

    public static EnviarCorreoRequest toBloqueoCuentaRequest(Cuenta cuenta, BloqueoCuenta bloqueo) {
        Cliente cliente = cuenta.getCliente();

        String nombreCliente = resolverNombreCliente(cliente);
        String cuentaEnmascarada = enmascararCuenta(cuenta.getNumeroCuenta());
        String montoBloqueado = formatearMoneda(bloqueo.getMontoBloqueado());
        String motivo = resolverTexto(bloqueo.getMotivo());
        String autoridadOrdenante = resolverTexto(bloqueo.getAutoridadOrdenante());
        String fechaEnvio = LocalDateTime.now().format(FORMATO_FECHA);

        String contenido = construirTemplateHtml(
                "Notificacion de bloqueo de cuenta",
                "Cuenta bloqueada correctamente",
                "Monto bloqueado",
                montoBloqueado,
                "Cliente",
                nombreCliente,
                "Cuenta",
                cuentaEnmascarada,
                "Motivo",
                motivo,
                "Autoridad ordenante",
                autoridadOrdenante,
                fechaEnvio,
                "Este correo fue generado automaticamente por Banco BanQuito como constancia informativa del bloqueo aplicado sobre su cuenta."
        );

        return new EnviarCorreoRequest(cliente.getEmail(), ASUNTO_BLOQUEO_CUENTA, contenido);
    }

    public static EnviarCorreoRequest toCambioEstadoCriticoRequest(
            Cuenta cuenta,
            EstadoCuentaEnum estadoAnterior,
            EstadoCuentaEnum estadoNuevo,
            String motivo
    ) {
        Cliente cliente = cuenta.getCliente();

        String nombreCliente = resolverNombreCliente(cliente);
        String cuentaEnmascarada = enmascararCuenta(cuenta.getNumeroCuenta());
        String estadoAnteriorTexto = estadoAnterior != null ? estadoAnterior.name() : "NO_DEFINIDO";
        String estadoNuevoTexto = estadoNuevo != null ? estadoNuevo.name() : "NO_DEFINIDO";
        String motivoCambio = resolverTexto(motivo);
        String fechaEnvio = LocalDateTime.now().format(FORMATO_FECHA);

        String contenido = construirTemplateHtml(
                "Notificacion de cambio de estado",
                "Estado de cuenta actualizado",
                "Nuevo estado",
                estadoNuevoTexto,
                "Cliente",
                nombreCliente,
                "Cuenta",
                cuentaEnmascarada,
                "Estado anterior",
                estadoAnteriorTexto,
                "Motivo",
                motivoCambio,
                fechaEnvio,
                "Este correo fue generado automaticamente por Banco BanQuito como constancia informativa del cambio de estado aplicado sobre su cuenta."
        );

        return new EnviarCorreoRequest(cliente.getEmail(), ASUNTO_CAMBIO_ESTADO_CUENTA, contenido);
    }

    private static String construirTemplateHtml(
            String subtitulo,
            String titulo,
            String etiquetaPrincipal,
            String valorPrincipal,
            String etiquetaFila1,
            String valorFila1,
            String etiquetaFila2,
            String valorFila2,
            String etiquetaFila3,
            String valorFila3,
            String etiquetaFila4,
            String valorFila4,
            String fechaEnvio,
            String textoPie
    ) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <body style="margin:0;padding:0;background-color:#f4f7fa;font-family:Arial,Helvetica,sans-serif;color:#1f2937;">
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color:#f4f7fa;padding:24px 12px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="width:600px;max-width:600px;background-color:#ffffff;border-collapse:collapse;border-radius:12px;overflow:hidden;">
                                <tr>
                                    <td style="background-color:#1f4f82;padding:24px 32px;text-align:center;">
                                        <span style="color:#ffffff;font-size:28px;font-weight:700;letter-spacing:0.5px;">Banco BanQuito</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:32px;">
                                        <p style="margin:0 0 8px 0;font-size:14px;color:#5b6b7f;">_SUBTITULO_</p>
                                        <h1 style="margin:0 0 24px 0;font-size:28px;line-height:36px;color:#111827;">_TITULO_</h1>
                                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="margin-bottom:24px;background-color:#eef5fb;border:1px solid #c9dcef;border-radius:10px;">
                                            <tr>
                                                <td style="padding:24px;text-align:center;">
                                                    <p style="margin:0 0 10px 0;font-size:13px;text-transform:uppercase;letter-spacing:1px;color:#1f4f82;">_ETIQUETA_PRINCIPAL_</p>
                                                    <p style="margin:0;font-size:34px;font-weight:700;color:#245d99;">_VALOR_PRINCIPAL_</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;">
                                            <tr>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#5b6b7f;width:42%;">_ETIQUETA_FILA_1_</td>
                                                <td style="padding:12px 0;border-bottom:1px solid #e5e7eb;font-size:14px;color:#111827;font-weight:600;">_VALOR_FILA_1_</td>
                                            </tr>
                                            <tr>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#5b6b7f;">_ETIQUETA_FILA_2_</td>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#111827;font-weight:600;">_VALOR_FILA_2_</td>
                                            </tr>
                                            <tr>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#5b6b7f;">_ETIQUETA_FILA_3_</td>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#111827;font-weight:600;">_VALOR_FILA_3_</td>
                                            </tr>
                                            <tr>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#5b6b7f;">_ETIQUETA_FILA_4_</td>
                                                <td style="padding:12px 0;border-bottom:1px solid #dbe5ef;font-size:14px;color:#111827;font-weight:600;">_VALOR_FILA_4_</td>
                                            </tr>
                                            <tr>
                                                <td style="padding:12px 0 0 0;font-size:14px;color:#5b6b7f;">Fecha de envio</td>
                                                <td style="padding:12px 0 0 0;font-size:14px;color:#111827;font-weight:600;">_FECHA_ENVIO_</td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:0 32px 28px 32px;">
                                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background-color:#f7fafd;border:1px solid #dbe5ef;border-radius:10px;">
                                            <tr>
                                                <td style="padding:18px 20px;font-size:13px;line-height:20px;color:#4f6276;">
                                                    _TEXTO_PIE_
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """
                .replace("_SUBTITULO_", escaparHtml(subtitulo))
                .replace("_TITULO_", escaparHtml(titulo))
                .replace("_ETIQUETA_PRINCIPAL_", escaparHtml(etiquetaPrincipal))
                .replace("_VALOR_PRINCIPAL_", escaparHtml(valorPrincipal))
                .replace("_ETIQUETA_FILA_1_", escaparHtml(etiquetaFila1))
                .replace("_VALOR_FILA_1_", escaparHtml(valorFila1))
                .replace("_ETIQUETA_FILA_2_", escaparHtml(etiquetaFila2))
                .replace("_VALOR_FILA_2_", escaparHtml(valorFila2))
                .replace("_ETIQUETA_FILA_3_", escaparHtml(etiquetaFila3))
                .replace("_VALOR_FILA_3_", escaparHtml(valorFila3))
                .replace("_ETIQUETA_FILA_4_", escaparHtml(etiquetaFila4))
                .replace("_VALOR_FILA_4_", escaparHtml(valorFila4))
                .replace("_FECHA_ENVIO_", escaparHtml(fechaEnvio))
                .replace("_TEXTO_PIE_", escaparHtml(textoPie));
    }

    private static String resolverNombreCliente(Cliente cliente) {
        if (cliente == null) {
            return "Cliente BanQuito";
        }

        if (cliente.getTipoCliente() == TipoClienteEnum.JURIDICO && cliente.getRazonSocial() != null) {
            return cliente.getRazonSocial();
        }

        String nombres = cliente.getNombres() != null ? cliente.getNombres() : "";
        String apellidos = cliente.getApellidos() != null ? cliente.getApellidos() : "";
        String nombreCompleto = (nombres + " " + apellidos).trim();

        return nombreCompleto.isBlank() ? "Cliente BanQuito" : nombreCompleto;
    }

    private static String resolverTexto(String valor) {
        return valor == null || valor.isBlank() ? "No especificado" : valor;
    }

    private static String enmascararCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.length() <= 4) {
            return "****";
        }

        return "****" + numeroCuenta.substring(numeroCuenta.length() - 4);
    }

    private static String formatearMoneda(BigDecimal valor) {
        if (valor == null) {
            return "$0.00";
        }

        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
        return formato.format(valor);
    }

    private static String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}