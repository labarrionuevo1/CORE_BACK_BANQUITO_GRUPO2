package com.banquito.core.customers.service.impl;

import com.banquito.core.customers.dto.request.ClienteRequest;
import com.banquito.core.customers.dto.response.ClienteResponse;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.mapper.ClienteMapper;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.customers.service.ClienteService;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;

    @Override
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(ClienteMapper::toResponse).toList(); 
    }
    
    @Override
    public Cliente obtenerEntidad(Integer id) { 
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id)); 
    }
    
    @Override
    public ClienteResponse obtener(Integer id) {
        return ClienteMapper.toResponse(obtenerEntidad(id)); 
    }
    
    @Override
    public ClienteResponse obtenerPorIdentificacion(String identificacion) {
        return repository.findByIdentificacion(identificacion)
                .map(ClienteMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificación: " + identificacion));
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        
        validarIdentificacionUnica(request.tipoIdentificacion(), request.identificacion());
        validarDatosPorTipo(request);
        
        Cliente representante = request.representanteLegalId() != null ? obtenerEntidad(request.representanteLegalId()) : null;
        Cliente cliente = new Cliente();
        cliente.setSubtipoClienteId(request.subtipoClienteId());
        cliente.setTipoCliente(request.tipoCliente());
        cliente.setTipoIdentificacion(request.tipoIdentificacion());
        cliente.setIdentificacion(request.identificacion());
        cliente.setNombres(request.nombres());
        cliente.setApellidos(request.apellidos());
        cliente.setRazonSocial(request.razonSocial());
        cliente.setFechaNacimiento(request.fechaNacimiento());
        cliente.setFechaConstitucion(request.fechaConstitucion());
        cliente.setRepresentanteLegal(representante);
        cliente.setEmail(request.email());
        cliente.setTelefonoMovil(request.telefonoMovil());
        cliente.setDireccion(request.direccion());
        cliente.setLatitud(request.latitud());
        cliente.setLongitud(request.longitud());
        cliente.setActivoPagosMasivos(Boolean.TRUE.equals(request.activoPagosMasivos()));
        cliente.setEstado(EstadoClienteEnum.ACTIVO);
        
        ClienteResponse response = ClienteMapper.toResponse(repository.save(cliente));
        return response;
    }
    
    @Override
    public ClienteResponse cambiarEstado(Integer id, EstadoClienteEnum nuevoEstado) {
        Cliente cliente = obtenerEntidad(id);
        cliente.setEstado(nuevoEstado);
        ClienteResponse response = ClienteMapper.toResponse(repository.save(cliente));
        return response;
    }
    
    @Override
    public boolean validarEmpresaParaPagosMasivos(String ruc) {
        List<Cliente> empresasValidas = repository.findByTipoClienteAndActivoPagosMasivosAndEstado(
                TipoClienteEnum.JURIDICO, true, EstadoClienteEnum.ACTIVO);
        
        boolean esValida = empresasValidas.stream()
                .anyMatch(cliente -> cliente.getIdentificacion().equals(ruc));
        return esValida;
    }

    private void validarIdentificacionUnica(Object tipoIdentificacion, String identificacion) {
        repository.findByTipoIdentificacionAndIdentificacion((TipoIdentificacionEnum) tipoIdentificacion, identificacion)
                .ifPresent(cliente -> {
                    throw new BusinessException("Ya existe un cliente con identificación: " + identificacion);
                });
    }

    private void validarDatosPorTipo(ClienteRequest r) {
        if (r.tipoCliente() == TipoClienteEnum.NATURAL && (r.nombres() == null || r.apellidos() == null || r.fechaNacimiento() == null)) {
            throw new BusinessException("Cliente NATURAL requiere nombres, apellidos y fecha de nacimiento");
        }
        if (r.tipoCliente() == TipoClienteEnum.JURIDICO && (r.razonSocial() == null || r.fechaConstitucion() == null)) {
            throw new BusinessException("Cliente JURIDICO requiere razón social y fecha de constitución");
        }
        if (r.tipoCliente() == TipoClienteEnum.NATURAL && Boolean.TRUE.equals(r.activoPagosMasivos())) {
            throw new BusinessException("Solo clientes jurídicos pueden activar pagos masivos");
        }
        if (r.tipoCliente() == TipoClienteEnum.NATURAL && r.razonSocial() != null) {
            throw new BusinessException("Cliente NATURAL no debe tener razón social");
        }
        if (r.tipoCliente() == TipoClienteEnum.JURIDICO && (r.nombres() != null || r.apellidos() != null)) {
            throw new BusinessException("Cliente JURIDICO no debe tener nombres y apellidos");
        }
    }
}
