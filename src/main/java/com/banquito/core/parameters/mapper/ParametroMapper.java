package com.banquito.core.parameters.mapper;

import com.banquito.core.parameters.dto.api.FeriadoRequest;
import com.banquito.core.parameters.dto.api.FeriadoResponse;
import com.banquito.core.parameters.dto.api.DiaHabilResponse;
import com.banquito.core.parameters.dto.api.ParametroCoreResponse;
import com.banquito.core.parameters.model.Feriado;
import com.banquito.core.parameters.model.ParametroCore;

public final class ParametroMapper {
    private ParametroMapper() {}
    
    public static ParametroCoreResponse toResponse(ParametroCore p) {
        return new ParametroCoreResponse(p.getCodigo(), p.getNombre(), p.getValorTexto(), p.getTipoDato(), p.getDescripcion());
    }
    
    public static FeriadoResponse toResponse(Feriado f) {
        return new FeriadoResponse(f.getFechaFeriado(), f.getNombre(), f.getEsFinSemana(), f.getEstado());
    }

    public static DiaHabilResponse toDiaHabilResponse(java.time.LocalDate fechaOriginal, java.time.LocalDate siguienteDiaHabil, Integer diasCalculados, String mensaje) {
        return new DiaHabilResponse(fechaOriginal, siguienteDiaHabil, diasCalculados, mensaje);
    }

    public static Feriado toEntity(FeriadoRequest request) {
        if (request == null) {
            return null;
        }

        Feriado feriado = new Feriado();
        feriado.setFechaFeriado(request.fecha());
        feriado.setNombre(request.nombre());
        return feriado;
    }
}
