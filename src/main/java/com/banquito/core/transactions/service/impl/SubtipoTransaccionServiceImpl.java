package com.banquito.core.transactions.service.impl;

import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import com.banquito.core.transactions.dto.api.SubtipoTransaccionResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.model.SubtipoTransaccion;
import com.banquito.core.transactions.repository.SubtipoTransaccionRepository;
import com.banquito.core.transactions.service.SubtipoTransaccionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubtipoTransaccionServiceImpl implements SubtipoTransaccionService {

    private final SubtipoTransaccionRepository subtipoTransaccionRepository;

    public SubtipoTransaccionServiceImpl(SubtipoTransaccionRepository subtipoTransaccionRepository) {
        this.subtipoTransaccionRepository = subtipoTransaccionRepository;
    }

    @Override
    public List<SubtipoTransaccionResponse> obtenerPorTipoMovimiento(TipoMovimientoEnum tipoMovimiento) {
        List<SubtipoTransaccion> subtipos = subtipoTransaccionRepository
                .findByTipoMovimientoBaseAndEstado(tipoMovimiento, EstadoCatalogoEnum.ACTIVO);

        return subtipos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubtipoTransaccionResponse> obtenerTodos() {
        return subtipoTransaccionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SubtipoTransaccionResponse toResponse(SubtipoTransaccion subtipo) {
        return new SubtipoTransaccionResponse(
                subtipo.getId(),
                subtipo.getCodigo(),
                subtipo.getNombre()
        );
    }
}
