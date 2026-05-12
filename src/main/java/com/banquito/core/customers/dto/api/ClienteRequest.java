package com.banquito.core.customers.dto.api;

import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteRequest(
        @NotNull Integer subtipoClienteId,
        @NotNull TipoClienteEnum tipoCliente,
        @NotNull TipoIdentificacionEnum tipoIdentificacion,
        @NotBlank @Size(max = 20) String identificacion,
        String nombres,
        String apellidos,
        String razonSocial,
        LocalDate fechaNacimiento,
        LocalDate fechaConstitucion,
        Integer representanteLegalId,
        @Email @NotBlank String email,
        @NotBlank String telefonoMovil,
        @NotBlank String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        Boolean activoPagosMasivos
) {}
