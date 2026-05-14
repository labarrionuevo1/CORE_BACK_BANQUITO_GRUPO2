package com.banquito.core.audit.service;

import com.banquito.core.audit.dto.AuditoriaEventoResponse;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.shared.enums.CanalOrigenEnum;

import java.util.List;

public interface AuditoriaService {

    List<AuditoriaEventoResponse> listar();

    List<AuditoriaEventoResponse> consultarPorEntidad(String entidad, String entidadId);

    List<AuditoriaEventoResponse> consultarPorModulo(String modulo);

    void registrarEvento(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            ResultadoAuditoriaEnum resultado,
            CanalOrigenEnum canalOrigen,
            String detalleJson
    );

    void registrarEventoNuevaTransaccion(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            ResultadoAuditoriaEnum resultado,
            CanalOrigenEnum canalOrigen,
            String detalleJson
    );
}