package com.banquito.core.notifications.mapper;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.BloqueoCuenta;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.notifications.dto.internal.EnviarCorreoRequest;

public final class NotificacionMapper {

    private NotificacionMapper() {
    }

    public static EnviarCorreoRequest toBloqueoCuentaRequest(Cuenta cuenta, BloqueoCuenta bloqueo) {
        Cliente cliente = cuenta.getCliente();
        String nombreCliente = resolverNombreCliente(cliente);
        String asunto = "Notificacion de bloqueo de cuenta BanQuito";
        String contenido = "Estimado/a " + nombreCliente + ",\n\n" +
                "Banco BanQuito le informa que se ha registrado un bloqueo sobre su cuenta " +
                cuenta.getNumeroCuenta() + ".\n\n" +
                "Monto bloqueado: " + bloqueo.getMontoBloqueado() + "\n" +
                "Motivo: " + resolverTexto(bloqueo.getMotivo()) + "\n" +
                "Autoridad ordenante: " + resolverTexto(bloqueo.getAutoridadOrdenante()) + "\n\n" +
                "Si requiere mayor informacion, acerquese a una agencia BanQuito o comuniquese con su ejecutivo.\n\n" +
                "Banco BanQuito";

        return new EnviarCorreoRequest(cliente.getEmail(), asunto, contenido);
    }

    public static EnviarCorreoRequest toCambioEstadoCriticoRequest(
            Cuenta cuenta,
            EstadoCuentaEnum estadoAnterior,
            EstadoCuentaEnum estadoNuevo,
            String motivo
    ) {
        Cliente cliente = cuenta.getCliente();
        String nombreCliente = resolverNombreCliente(cliente);
        String asunto = "Notificacion de cambio de estado de cuenta BanQuito";
        String contenido = "Estimado/a " + nombreCliente + ",\n\n" +
                "Banco BanQuito le informa que el estado de su cuenta " + cuenta.getNumeroCuenta() +
                " cambio de " + estadoAnterior + " a " + estadoNuevo + ".\n\n" +
                "Motivo: " + resolverTexto(motivo) + "\n\n" +
                "Si no reconoce este cambio o requiere mayor informacion, comuniquese con Banco BanQuito.\n\n" +
                "Banco BanQuito";

        return new EnviarCorreoRequest(cliente.getEmail(), asunto, contenido);
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
}
