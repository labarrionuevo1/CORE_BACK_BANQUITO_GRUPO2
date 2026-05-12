package com.banquito.core.customers.dto.api;

import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;

public record ClienteResponse(
        Integer id,
        Integer subtipoClienteId,
        TipoClienteEnum tipoCliente,
        TipoIdentificacionEnum tipoIdentificacion,
        String identificacion,
        String nombreVisual,
        String email,
        String telefonoMovil,
        EstadoClienteEnum estado,
        Boolean activoPagosMasivos
) {}
