package com.banquito.core.institutional.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.banquito.core.institutional.dto.api.CuentaInstitucionalResponse;
import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.institutional.mapper.CuentaInstitucionalMapper;
import com.banquito.core.institutional.model.CuentaInstitucional;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.institutional.service.CuentaInstitucionalService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.shared.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaInstitucionalServiceImpl implements CuentaInstitucionalService {

    private final CuentaInstitucionalRepository repository;

    @Override
    public List<CuentaInstitucionalResponse> listar() {
        return repository.findAll()
                .stream()
                .map(CuentaInstitucionalMapper::toResponse)
                .toList();
    }

    @Override
    public CuentaInstitucionalResponse porCodigo(String codigo) {
        return CuentaInstitucionalMapper.toResponse(obtenerPorCodigo(codigo));
    }

    @Override
    public CuentaInstitucionalResponse porNumeroCuenta(String numeroCuenta) {
        return CuentaInstitucionalMapper.toResponse(obtenerPorNumeroCuenta(numeroCuenta));
    }

    @Override
    public CuentaInstitucional obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada: " + codigo));
    }

    @Override
    public CuentaInstitucional obtenerPorNumeroCuenta(String numeroCuenta) {
        return repository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada: " + numeroCuenta));
    }

    @Override
    public CuentaInstitucional validarActivaPorCodigo(String codigo) {
        CuentaInstitucional cuenta = obtenerPorCodigo(codigo);

        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + codigo);
        }

        return cuenta;
    }

    @Override
    public CuentaInstitucional validarActivaPorNumeroCuenta(String numeroCuenta) {
        CuentaInstitucional cuenta = obtenerPorNumeroCuenta(numeroCuenta);

        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + numeroCuenta);
        }

        return cuenta;
    }
}