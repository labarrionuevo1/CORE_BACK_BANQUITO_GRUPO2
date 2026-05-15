package com.banquito.core.transactions.dto.api;

import com.banquito.core.shared.enums.CanalOrigenEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferenciaRequest(

        @NotBlank(message = "La cuenta origen es obligatoria")
        @Size(max = 30, message = "La cuenta origen no puede superar 30 caracteres")
        String cuentaOrigen,

        @NotBlank(message = "La cuenta destino es obligatoria")
        @Size(max = 30, message = "La cuenta destino no puede superar 30 caracteres")
        String cuentaDestino,

        @NotBlank(message = "El codigo de subtipo de transaccion es obligatorio")
        @Size(max = 40, message = "El codigo de subtipo no puede superar 40 caracteres")
        String codigoSubtipo,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotNull(message = "El UUID de operacion es obligatorio")
        UUID uuidOperacion,

        UUID uuidGrupoOperacion,

        @Size(max = 100, message = "La referencia externa no puede superar 100 caracteres")
        String referenciaExterna,

        @Size(max = 20, message = "El numero de comprobante no puede superar 20 caracteres")
        String numeroComprobante,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
        String descripcion,

        CanalOrigenEnum canalOrigen,

        LocalDate fechaNegocio,

        Integer usuarioCoreId,

        Integer credencialWebId
) {
}