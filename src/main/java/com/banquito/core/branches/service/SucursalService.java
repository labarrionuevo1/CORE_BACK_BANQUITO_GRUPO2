package com.banquito.core.branches.service;

import com.banquito.core.branches.dto.request.SucursalRequest;
import com.banquito.core.branches.dto.response.SucursalResponse;
import com.banquito.core.branches.enums.EstadoSucursalEnum;
import com.banquito.core.branches.mapper.SucursalMapper;
import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.branches.repository.SucursalRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SucursalService {
    private final SucursalRepository repository;

    public List<SucursalResponse> listar() { 
        return repository.findAll().stream().map(SucursalMapper::toResponse).toList(); 
    }
    
    public List<SucursalResponse> listarActivas() { 
        log.info("Listando sucursales activas");
        return repository.findByEstado(EstadoSucursalEnum.ACTIVA).stream()
                .map(SucursalMapper::toResponse).toList(); 
    }
    
    public Sucursal obtenerEntidad(Integer id) { 
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + id)); 
    }
    
    public SucursalResponse obtener(Integer id) { 
        return SucursalMapper.toResponse(obtenerEntidad(id)); 
    }
    
    public SucursalResponse obtenerPorCodigo(String codigoSucursal) { 
        log.info("Buscando sucursal por código: {}", codigoSucursal);
        return repository.findByCodigoSucursal(codigoSucursal)
                .map(SucursalMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con código: " + codigoSucursal)); 
    }
    
    public SucursalResponse crear(SucursalRequest request) {
        Sucursal s = new Sucursal();
        s.setCodigoSucursal(request.codigoSucursal());
        s.setNombre(request.nombre());
        s.setCiudad(request.ciudad());
        s.setDireccion(request.direccion());
        return SucursalMapper.toResponse(repository.save(s));
    }
}
