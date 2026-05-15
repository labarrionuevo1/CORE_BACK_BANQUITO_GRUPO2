package com.banquito.core.parameters.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.parameters.dto.api.FeriadoRequest;
import com.banquito.core.parameters.dto.api.FeriadoResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.model.Feriado;
import com.banquito.core.parameters.repository.FeriadoRepository;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriadoServiceImpl implements FeriadoService {

    private static final String MODULO_PARAMETERS = "PARAMETERS";

    private final FeriadoRepository repository;
    private final AuditoriaService auditoriaService;
    
    @Override
    @Transactional(readOnly = true)
    public List<FeriadoResponse> listar() {
        return repository.findAll().stream().map(ParametroMapper::toResponse).toList(); 
    }
    
    @Override
    @Transactional(readOnly = true)
    public FeriadoResponse obtener(LocalDate fecha) {
        return repository.findById(fecha).map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Feriado no encontrado: " + fecha)); 
    }
    
    @Override
    @Transactional(readOnly = true)
    public LocalDate calcularSiguienteDiaHabil(LocalDate fecha) {
        LocalDate siguienteDia = fecha.plusDays(1);
        
        while (!esDiaHabil(siguienteDia)) {
            siguienteDia = siguienteDia.plusDays(1);
        }

        return siguienteDia;
    }

    @Override
    @Transactional
    public FeriadoResponse crear(FeriadoRequest request) {
        if (repository.existsById(request.fecha())) {
            throw new BusinessException("Ya existe un feriado para la fecha: " + request.fecha());
        }

        Feriado feriado = ParametroMapper.toEntity(request);
        feriado.setEsFinSemana(
            request.fecha().getDayOfWeek() == DayOfWeek.SATURDAY ||
            request.fecha().getDayOfWeek() == DayOfWeek.SUNDAY
        );
        
        if (request.estado() != null) {
            feriado.setEstado(EstadoCatalogoEnum.valueOf(request.estado()));
        } else {
            feriado.setEstado(EstadoCatalogoEnum.ACTIVO);
        }

        Feriado feriadoGuardado = repository.save(feriado);

        auditoriaService.registrarEvento(
                MODULO_PARAMETERS,
                "CREAR_FERIADO",
                "FERIADO",
                feriadoGuardado.getFechaFeriado().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"nombre\":\"" + feriadoGuardado.getNombre() + "\"}"
        );

        return ParametroMapper.toResponse(feriadoGuardado);
    }

    @Override
    @Transactional
    public FeriadoResponse actualizar(LocalDate fecha, FeriadoRequest request) {
        if (!fecha.equals(request.fecha())) {
            eliminar(fecha);
            return crear(request);
        } else {
            Feriado feriado = repository.findById(fecha)
                    .orElseThrow(() -> new ResourceNotFoundException("Feriado no encontrado: " + fecha));

            feriado.setNombre(request.nombre());
            if (request.estado() != null) {
                feriado.setEstado(EstadoCatalogoEnum.valueOf(request.estado()));
            }
            
            feriado.setEsFinSemana(
                request.fecha().getDayOfWeek() == DayOfWeek.SATURDAY ||
                request.fecha().getDayOfWeek() == DayOfWeek.SUNDAY
            );
            
            Feriado feriadoActualizado = repository.save(feriado);

            auditoriaService.registrarEvento(
                    MODULO_PARAMETERS,
                    "ACTUALIZAR_FERIADO",
                    "FERIADO",
                    feriadoActualizado.getFechaFeriado().toString(),
                    ResultadoAuditoriaEnum.EXITOSO,
                    CanalOrigenEnum.CORE,
                    "Feriado actualizado"
            );

            return ParametroMapper.toResponse(feriadoActualizado);
        }
    }

    @Override
    @Transactional
    public void eliminar(LocalDate fecha) {
        if (!repository.existsById(fecha)) {
            throw new ResourceNotFoundException("Feriado no encontrado: " + fecha);
        }
        repository.deleteById(fecha);

        auditoriaService.registrarEvento(
                MODULO_PARAMETERS,
                "ELIMINAR_FERIADO",
                "FERIADO",
                fecha.toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Feriado eliminado"
        );
    }
    
    private boolean esDiaHabil(LocalDate fecha) {
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        
        return repository.findById(fecha)
                .map(feriado -> feriado.getEstado() == EstadoCatalogoEnum.INACTIVO)
                .orElse(true);
    }
}
