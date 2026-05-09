package com.banquito.core.customers.service;

import com.banquito.core.customers.dto.request.ClienteRequest;
import com.banquito.core.customers.dto.response.ClienteResponse;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.mapper.ClienteMapper;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public List<ClienteResponse> listar() { return repository.findAll().stream().map(ClienteMapper::toResponse).toList(); }
    public Cliente obtenerEntidad(Integer id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id)); }
    public ClienteResponse obtener(Integer id) { return ClienteMapper.toResponse(obtenerEntidad(id)); }

    public ClienteResponse crear(ClienteRequest request) {
        validarDatosPorTipo(request);
        Cliente representante = request.representanteLegalId() != null ? obtenerEntidad(request.representanteLegalId()) : null;
        Cliente c = new Cliente();
        c.setSubtipoClienteId(request.subtipoClienteId());
        c.setTipoCliente(request.tipoCliente());
        c.setTipoIdentificacion(request.tipoIdentificacion());
        c.setIdentificacion(request.identificacion());
        c.setNombres(request.nombres());
        c.setApellidos(request.apellidos());
        c.setRazonSocial(request.razonSocial());
        c.setFechaNacimiento(request.fechaNacimiento());
        c.setFechaConstitucion(request.fechaConstitucion());
        c.setRepresentanteLegal(representante);
        c.setEmail(request.email());
        c.setTelefonoMovil(request.telefonoMovil());
        c.setDireccion(request.direccion());
        c.setLatitud(request.latitud());
        c.setLongitud(request.longitud());
        c.setActivoPagosMasivos(Boolean.TRUE.equals(request.activoPagosMasivos()));
        c.setEstado(EstadoClienteEnum.ACTIVO);
        return ClienteMapper.toResponse(repository.save(c));
    }

    private void validarDatosPorTipo(ClienteRequest r) {
        if (r.tipoCliente() == TipoClienteEnum.NATURAL && (r.nombres() == null || r.apellidos() == null || r.fechaNacimiento() == null)) {
            throw new BusinessException("Cliente NATURAL requiere nombres, apellidos y fecha de nacimiento");
        }
        if (r.tipoCliente() == TipoClienteEnum.JURIDICO && (r.razonSocial() == null || r.fechaConstitucion() == null)) {
            throw new BusinessException("Cliente JURIDICO requiere razon social y fecha de constitucion");
        }
        if (r.tipoCliente() == TipoClienteEnum.NATURAL && Boolean.TRUE.equals(r.activoPagosMasivos())) {
            throw new BusinessException("Solo clientes juridicos pueden activar pagos masivos");
        }
    }
}
