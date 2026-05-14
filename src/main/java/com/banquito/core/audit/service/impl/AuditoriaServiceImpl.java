package com.banquito.core.audit.service.impl;

import com.banquito.core.audit.dto.api.AuditoriaEventoResponse;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.mapper.AuditoriaMapper;
import com.banquito.core.audit.model.AuditoriaEvento;
import com.banquito.core.audit.repository.AuditoriaEventoRepository;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaEventoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> consultarPorEntidad(String entidad, String entidadId) {
        return repository.findByEntidadAndEntidadIdOrderByFechaEventoDesc(entidad, entidadId)
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> consultarPorModulo(String modulo) {
        return repository.findByModuloOrderByFechaEventoDesc(modulo)
                .stream()
                .map(AuditoriaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
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

    @Override
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
}