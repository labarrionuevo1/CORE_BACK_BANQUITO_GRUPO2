package com.banquito.core.audit.mapper;

import com.banquito.core.audit.dto.response.AuditoriaEventoResponse;
import com.banquito.core.audit.model.AuditoriaEvento;

public final class AuditoriaMapper {
    private AuditoriaMapper() {}
    public static AuditoriaEventoResponse toResponse(AuditoriaEvento a) {
        return new AuditoriaEventoResponse(a.getId(), a.getModulo(), a.getAccion(), a.getEntidad(), a.getEntidadId(), a.getResultado(), a.getCanalOrigen(), a.getFechaEvento());
    }
}
