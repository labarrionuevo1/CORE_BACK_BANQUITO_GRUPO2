package com.banquito.core.customers.mapper;

import com.banquito.core.customers.dto.response.ClienteResponse;
import com.banquito.core.customers.model.Cliente;

public final class ClienteMapper {
    private ClienteMapper() {}
    public static ClienteResponse toResponse(Cliente c) {
        String nombre = c.getTipoCliente().name().equals("JURIDICO") ? c.getRazonSocial() : (c.getNombres() + " " + c.getApellidos()).trim();
        return new ClienteResponse(c.getId(), c.getSubtipoClienteId(), c.getTipoCliente(), c.getTipoIdentificacion(), c.getIdentificacion(), nombre,
                c.getEmail(), c.getTelefonoMovil(), c.getEstado(), c.getActivoPagosMasivos());
    }
}
