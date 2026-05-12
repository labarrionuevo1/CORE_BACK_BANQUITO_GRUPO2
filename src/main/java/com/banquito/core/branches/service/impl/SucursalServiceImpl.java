package com.banquito.core.branches.service.impl;

import com.banquito.core.branches.dto.api.SucursalRequest;
import com.banquito.core.branches.dto.api.SucursalResponse;
import com.banquito.core.branches.enums.EstadoSucursalEnum;
import com.banquito.core.branches.mapper.SucursalMapper;
import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.branches.repository.SucursalRepository;
import com.banquito.core.branches.service.SucursalService;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {
    private final SucursalRepository repository;

    @Override
    public List<SucursalResponse> listar() { 
        return repository.findAll().stream().map(SucursalMapper::toResponse).toList(); 
    }
    
    @Override
    public List<SucursalResponse> listarActivas() {
        return repository.findByEstado(EstadoSucursalEnum.ACTIVA).stream()
                .map(SucursalMapper::toResponse).toList(); 
    }
    
    @Override
    public Sucursal obtenerEntidad(Integer id) { 
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id)); 
    }
    
    @Override
    public SucursalResponse obtener(Integer id) { 
        return SucursalMapper.toResponse(obtenerEntidad(id)); 
    }
    
    @Override
    public SucursalResponse obtenerPorCodigo(String codigoSucursal) {
        return repository.findByCodigoSucursal(codigoSucursal)
                .map(SucursalMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con código: " + codigoSucursal)); 
    }
    
    @Override
    public SucursalResponse crear(SucursalRequest request) {
        Sucursal sucursal = new Sucursal();
        sucursal.setCodigoSucursal(request.codigoSucursal());
        sucursal.setNombre(request.nombre());
        sucursal.setCiudad(request.ciudad());
        sucursal.setDireccion(request.direccion());
        return SucursalMapper.toResponse(repository.save(sucursal));
    }
}
