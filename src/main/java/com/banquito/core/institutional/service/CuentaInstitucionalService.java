package com.banquito.core.institutional.service;

import com.banquito.core.institutional.dto.CuentaInstitucionalResponse;
import com.banquito.core.institutional.mapper.CuentaInstitucionalMapper;
import com.banquito.core.institutional.model.CuentaInstitucional;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.shared.exception.ValidationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaInstitucionalService {

    private final CuentaInstitucionalRepository repository;

    
    public List<CuentaInstitucionalResponse> listar() {
        return repository.findAll()
                .stream()
                .map(CuentaInstitucionalMapper::toResponse)
                .toList();
    }

    public CuentaInstitucionalResponse porCodigo(String codigo) {
        return CuentaInstitucionalMapper.toResponse(obtenerPorCodigo(codigo));
    }

    public CuentaInstitucionalResponse porNumeroCuenta(String numeroCuenta) {
        return CuentaInstitucionalMapper.toResponse(obtenerPorNumeroCuenta(numeroCuenta));
    }

    public CuentaInstitucional obtenerPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada: " + codigo));
    }

    public CuentaInstitucional obtenerPorNumeroCuenta(String numeroCuenta) {
        return repository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada: " + numeroCuenta));

            }
    public CuentaInstitucional validarActivaPorCodigo(String codigo) {
        CuentaInstitucional cuenta = obtenerPorCodigo(codigo);

        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + codigo);
        }

        return cuenta;
    }

    public CuentaInstitucional validarActivaPorNumeroCuenta(String numeroCuenta) {
        CuentaInstitucional cuenta = obtenerPorNumeroCuenta(numeroCuenta);

        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + numeroCuenta);
        }

        return cuenta;
    }
}