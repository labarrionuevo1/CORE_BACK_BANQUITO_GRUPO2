package com.banquito.core.transactions.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaResponse(
        String estado,
        UUID uuidDebitoCore,
        UUID uuidCreditoCore,
        UUID uuidGrupoOperacion,
        BigDecimal saldoDisponibleOrigen
) {}
