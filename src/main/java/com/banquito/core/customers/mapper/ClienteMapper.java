package com.banquito.core.customers.mapper;

import com.banquito.core.customers.dto.api.ClienteResponse;
import com.banquito.core.customers.dto.api.ClienteValidacionResponse;
import com.banquito.core.customers.model.Cliente;

public final class ClienteMapper {
    private ClienteMapper() {}
    public static ClienteResponse toResponse(Cliente c) {
        Integer representanteId = c.getRepresentanteLegal() != null ? c.getRepresentanteLegal().getId() : null;
        return new ClienteResponse(
                c.getId(),
                c.getSubtipoClienteId(),
                c.getTipoCliente(),
                c.getTipoIdentificacion(),
                c.getIdentificacion(),
                c.getNombres(),
                c.getApellidos(),
                c.getRazonSocial(),
                c.getFechaNacimiento(),
                c.getFechaConstitucion(),
                representanteId,
                c.getEmail(),
                c.getTelefonoMovil(),
                c.getDireccion(),
                c.getLatitud(),
                c.getLongitud(),
                c.getEstado(),
                c.getActivoPagosMasivos()
        );


    public static ClienteValidacionResponse toValidacionResponse(String ruc, boolean esValida, String mensaje, String motivo) {
        return new ClienteValidacionResponse(ruc, esValida, mensaje, motivo);
    }
}
