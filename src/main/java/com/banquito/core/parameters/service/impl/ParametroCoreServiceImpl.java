package com.banquito.core.parameters.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.parameters.dto.api.ParametroCoreRequest;
import com.banquito.core.parameters.dto.api.ParametroCoreResponse;
import com.banquito.core.parameters.mapper.ParametroMapper;
import com.banquito.core.parameters.model.ParametroCore;
import com.banquito.core.parameters.repository.ParametroCoreRepository;
import com.banquito.core.parameters.service.ParametroCoreService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroCoreServiceImpl implements ParametroCoreService {

    private static final String MODULO_PARAMETERS = "PARAMETERS";

    private final ParametroCoreRepository repository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public List<ParametroCoreResponse> listar() {
        return repository.findAll().stream()
                .map(ParametroMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroCoreResponse> listarActivos() {
        return listar();
    }

    @Override
    @Transactional(readOnly = true)
    public ParametroCoreResponse obtener(String codigo) {
        return repository.findById(codigo)
                .map(ParametroMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro no encontrado: " + codigo));
    }

    @Override
    @Transactional
    public ParametroCoreResponse crear(ParametroCoreRequest request) {
        if (repository.existsById(request.codigo())) {
            throw new BusinessException("Ya existe un parámetro con el código: " + request.codigo());
        }

        ParametroCore parametro = ParametroMapper.toEntity(request);
        ParametroCore parametroGuardado = repository.save(parametro);

        auditoriaService.registrarEvento(
                MODULO_PARAMETERS,
                "CREAR_PARAMETRO",
                "PARAMETRO_CORE",
                parametroGuardado.getCodigo(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Parámetro creado"
        );

        return ParametroMapper.toResponse(parametroGuardado);
    }

    @Override
    @Transactional
    public ParametroCoreResponse actualizar(String codigo, ParametroCoreRequest request) {
        ParametroCore parametro = repository.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro no encontrado: " + codigo));

        parametro.setNombre(request.nombre());
        parametro.setValorTexto(request.valor());
        parametro.setTipoDato(com.banquito.core.parameters.enums.TipoDatoParametroEnum.valueOf(request.tipoDato()));
        parametro.setDescripcion(request.descripcion());

        ParametroCore parametroActualizado = repository.save(parametro);

        auditoriaService.registrarEvento(
                MODULO_PARAMETERS,
                "ACTUALIZAR_PARAMETRO",
                "PARAMETRO_CORE",
                parametroActualizado.getCodigo(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Parámetro actualizado"
        );

        return ParametroMapper.toResponse(parametroActualizado);
    }

    @Override
    @Transactional
    public void eliminar(String codigo) {
        if (!repository.existsById(codigo)) {
            throw new ResourceNotFoundException("Parámetro no encontrado: " + codigo);
        }
        repository.deleteById(codigo);

        auditoriaService.registrarEvento(
                MODULO_PARAMETERS,
                "ELIMINAR_PARAMETRO",
                "PARAMETRO_CORE",
                codigo,
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Parámetro eliminado"
        );
    }
}