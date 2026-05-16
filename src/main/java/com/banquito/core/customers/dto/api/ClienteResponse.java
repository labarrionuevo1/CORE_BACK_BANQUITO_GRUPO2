package com.banquito.core.customers.dto.api;

import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteResponse(
    Integer id,
    Integer subtipoClienteId,
    TipoClienteEnum tipoCliente,
    TipoIdentificacionEnum tipoIdentificacion,
    String identificacion,
    String nombres,
    String apellidos,
    String razonSocial,
    LocalDate fechaNacimiento,
    LocalDate fechaConstitucion,
    Integer representanteLegalId,
    String email,
    String telefonoMovil,
    String direccion,
    BigDecimal latitud,
    BigDecimal longitud,
    EstadoClienteEnum estado,
    Boolean activoPagosMasivos
) {}
