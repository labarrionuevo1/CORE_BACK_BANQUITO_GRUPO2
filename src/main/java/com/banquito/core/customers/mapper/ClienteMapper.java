package com.banquito.core.customers.mapper;

import com.banquito.core.customers.dto.api.ClienteResponse;
import com.banquito.core.customers.dto.api.ClienteValidacionResponse;
import com.banquito.core.customers.model.Cliente;

public final class ClienteMapper {
    private ClienteMapper() {
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        String nombre = cliente.getTipoCliente().name().equals("JURIDICO") ? cliente.getRazonSocial() : (cliente.getNombres() + " " + cliente.getApellidos()).trim();
        return new ClienteResponse(
                cliente.getId(),
                cliente.getSubtipoClienteId(),
                cliente.getTipoCliente(),
                cliente.getTipoIdentificacion(),
                cliente.getIdentificacion(),
                nombre,
                cliente.getEmail(),
                cliente.getTelefonoMovil(),
                cliente.getEstado(),
                cliente.getActivoPagosMasivos());
    }

    public static ClienteValidacionResponse toValidacionResponse(String ruc, boolean esValida, String mensaje, String motivo) {
        return new ClienteValidacionResponse(ruc, esValida, mensaje, motivo);
    }
}
