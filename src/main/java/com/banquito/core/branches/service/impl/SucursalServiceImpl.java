package com.banquito.core.branches.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.branches.dto.api.SucursalRequest;
import com.banquito.core.branches.dto.api.SucursalResponse;
import com.banquito.core.branches.enums.EstadoSucursalEnum;
import com.banquito.core.branches.mapper.SucursalMapper;
import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.branches.repository.SucursalRepository;
import com.banquito.core.branches.service.SucursalService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private static final String MODULO_BRANCHES = "BRANCHES";

    private final SucursalRepository repository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> listar() {
        return repository.findAll()
                .stream()
                .map(SucursalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> listarActivas() {
        return repository.findByEstado(EstadoSucursalEnum.ACTIVA)
                .stream()
                .map(SucursalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Sucursal obtenerEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse obtener(Integer id) {
        return SucursalMapper.toResponse(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse obtenerPorCodigo(String codigoSucursal) {
        return repository.findByCodigoSucursal(codigoSucursal)
                .map(SucursalMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal no encontrada con codigo: " + codigoSucursal
                ));
    }

    @Override
    @Transactional
    public SucursalResponse crear(SucursalRequest request) {
        repository.findByCodigoSucursal(request.codigoSucursal())
                .ifPresent(sucursal -> {
                    throw new BusinessException(
                            "Ya existe una sucursal con codigo: " + request.codigoSucursal()
                    );
                });

        Sucursal sucursal = new Sucursal();
        sucursal.setCodigoSucursal(request.codigoSucursal());
        sucursal.setNombre(request.nombre());
        sucursal.setCiudad(request.ciudad());
        sucursal.setDireccion(request.direccion());

        Sucursal sucursalGuardada = repository.save(sucursal);

        auditoriaService.registrarEvento(
                MODULO_BRANCHES,
                "CREAR_SUCURSAL",
                "SUCURSAL",
                sucursalGuardada.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"codigoSucursal\":\"" + sanitizarJson(sucursalGuardada.getCodigoSucursal()) +
                        "\",\"nombre\":\"" + sanitizarJson(sucursalGuardada.getNombre()) + "\"}"
        );

        return SucursalMapper.toResponse(sucursalGuardada);
    }

    private String sanitizarJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}