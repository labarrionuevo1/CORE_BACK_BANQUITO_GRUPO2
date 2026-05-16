package com.banquito.core.integration.switchapi.dto.api;

import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;

public record ValidarEmpresaSwitchResponse(
        String ruc,
        Boolean existe,
        TipoClienteEnum tipoCliente,
        EstadoClienteEnum estado,
        Boolean activoPagosMasivos,
        Boolean credencialWebValida,
        Boolean habilitada,
        String codigo,
        String mensaje
) {
}