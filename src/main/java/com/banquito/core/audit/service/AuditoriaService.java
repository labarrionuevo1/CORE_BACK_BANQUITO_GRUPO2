package com.banquito.core.audit.service;

import com.banquito.core.audit.dto.response.AuditoriaEventoResponse;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.mapper.AuditoriaMapper;
import com.banquito.core.audit.model.AuditoriaEvento;
import com.banquito.core.audit.repository.AuditoriaEventoRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaEventoRepository repository;

    public List<AuditoriaEventoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    public List<AuditoriaEventoResponse> consultarPorEntidad(String entidad, String entidadId) {
        return repository.findByEntidadAndEntidadIdOrderByFechaEventoDesc(entidad, entidadId)
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    public List<AuditoriaEventoResponse> consultarPorModulo(String modulo) {
        return repository.findByModuloOrderByFechaEventoDesc(modulo)
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    public void registrarEvento(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            ResultadoAuditoriaEnum resultado,
            CanalOrigenEnum canalOrigen,
            String detalleJson
    ) {
        AuditoriaEvento evento = new AuditoriaEvento();
        evento.setModulo(modulo);
        evento.setAccion(accion);
        evento.setEntidad(entidad);
        evento.setEntidadId(entidadId);
        evento.setResultado(resultado);
        evento.setCanalOrigen(canalOrigen);
        evento.setDetalleJson(detalleJson);

        repository.save(evento);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEventoNuevaTransaccion(
            String modulo,
            String accion,
            String entidad,
            String entidadId,
            ResultadoAuditoriaEnum resultado,
            CanalOrigenEnum canalOrigen,
            String detalleJson
    ) {
        registrarEvento(
                modulo,
                accion,
                entidad,
                entidadId,
                resultado,
                canalOrigen,
                detalleJson
        );
    }
}