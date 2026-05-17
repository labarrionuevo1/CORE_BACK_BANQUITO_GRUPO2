package com.banquito.core.customers.service.impl;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.customers.dto.api.ClienteRequest;
import com.banquito.core.customers.dto.api.ClienteResponse;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.mapper.ClienteMapper;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.customers.model.SubtipoCliente;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.customers.repository.SubtipoClienteRepository;
import com.banquito.core.customers.service.ClienteService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private static final String MODULO_CUSTOMERS = "CUSTOMERS";

    private final ClienteRepository repository;
    private final SubtipoClienteRepository subtipoClienteRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerEntidad(Integer id) {
        return repository.findByIdWithRepresentanteLegal(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtener(Integer id) {
        return ClienteMapper.toResponse(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorIdentificacion(String identificacion) {
        return repository.findByIdentificacion(identificacion)
                .map(ClienteMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con identificacion: " + identificacion
                ));
    }

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        validarIdentificacionUnica(request.tipoIdentificacion(), request.identificacion());
        validarDatosPorTipo(request);
        validarSubtipoCliente(request.subtipoClienteId(), request.tipoCliente());

        Cliente representante = obtenerRepresentanteLegal(request);

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

        Cliente clienteGuardado = repository.save(cliente);

        auditoriaService.registrarEvento(
                MODULO_CUSTOMERS,
                "CREAR_CLIENTE",
                "CLIENTE",
                clienteGuardado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"tipoCliente\":\"" + clienteGuardado.getTipoCliente() +
                        "\",\"tipoIdentificacion\":\"" + clienteGuardado.getTipoIdentificacion() +
                        "\",\"identificacion\":\"" + sanitizarJson(clienteGuardado.getIdentificacion()) + "\"}"
        );

        return ClienteMapper.toResponse(clienteGuardado);
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente cliente = obtenerEntidad(id);
        validarDatosPorTipo(request);
        validarSubtipoCliente(request.subtipoClienteId(), request.tipoCliente());

        Cliente representante = obtenerRepresentanteLegal(request);

        cliente.setSubtipoClienteId(request.subtipoClienteId());
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

        Cliente clienteActualizado = repository.save(cliente);

        auditoriaService.registrarEvento(
                MODULO_CUSTOMERS,
                "ACTUALIZAR_CLIENTE",
                "CLIENTE",
                clienteActualizado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "Cliente actualizado"
        );

        return ClienteMapper.toResponse(clienteActualizado);
    }

    @Override
    @Transactional
    public ClienteResponse cambiarEstado(Integer id, EstadoClienteEnum nuevoEstado) {
        Cliente cliente = obtenerEntidad(id);
        EstadoClienteEnum estadoAnterior = cliente.getEstado();

        cliente.setEstado(nuevoEstado);

        Cliente clienteGuardado = repository.save(cliente);

        auditoriaService.registrarEvento(
                MODULO_CUSTOMERS,
                "CAMBIAR_ESTADO_CLIENTE",
                "CLIENTE",
                clienteGuardado.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"estadoAnterior\":\"" + estadoAnterior +
                        "\",\"estadoNuevo\":\"" + nuevoEstado + "\"}"
        );

        return ClienteMapper.toResponse(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarEmpresaParaPagosMasivos(String ruc) {
        return repository.findByTipoIdentificacionAndIdentificacion(TipoIdentificacionEnum.RUC, ruc)
                .filter(cliente -> cliente.getTipoCliente() == TipoClienteEnum.JURIDICO)
                .filter(cliente -> cliente.getEstado() == EstadoClienteEnum.ACTIVO)
                .filter(cliente -> Boolean.TRUE.equals(cliente.getActivoPagosMasivos()))
                .isPresent();
    }

    private void validarIdentificacionUnica(
            TipoIdentificacionEnum tipoIdentificacion,
            String identificacion
    ) {
        repository.findByTipoIdentificacionAndIdentificacion(tipoIdentificacion, identificacion)
                .ifPresent(cliente -> {
                    throw new BusinessException("Ya existe un cliente con identificacion: " + identificacion);
                });
    }

    private void validarDatosPorTipo(ClienteRequest request) {
        if (request.tipoCliente() == TipoClienteEnum.NATURAL) {
            validarClienteNatural(request);
        }

        if (request.tipoCliente() == TipoClienteEnum.JURIDICO) {
            validarClienteJuridico(request);
        }
    }

    private void validarClienteNatural(ClienteRequest request) {
        if (request.nombres() == null || request.apellidos() == null || request.fechaNacimiento() == null) {
            throw new BusinessException("Cliente NATURAL requiere nombres, apellidos y fecha de nacimiento");
        }

        if (request.tipoIdentificacion() == TipoIdentificacionEnum.RUC) {
            throw new BusinessException("Cliente NATURAL no puede usar identificacion tipo RUC");
        }

        if (Boolean.TRUE.equals(request.activoPagosMasivos())) {
            throw new BusinessException("Solo clientes juridicos pueden activar pagos masivos");
        }

        if (request.razonSocial() != null) {
            throw new BusinessException("Cliente NATURAL no debe tener razon social");
        }

        if (request.fechaConstitucion() != null) {
            throw new BusinessException("Cliente NATURAL no debe tener fecha de constitucion");
        }

        if (request.representanteLegalId() != null) {
            throw new BusinessException("Cliente NATURAL no debe tener representante legal");
        }
    }

    private void validarClienteJuridico(ClienteRequest request) {
        if (request.razonSocial() == null || request.fechaConstitucion() == null) {
            throw new BusinessException("Cliente JURIDICO requiere razon social y fecha de constitucion");
        }

        if (request.representanteLegalId() == null) {
            throw new BusinessException("Cliente JURIDICO requiere representante legal");
        }

        if (request.tipoIdentificacion() != TipoIdentificacionEnum.RUC) {
            throw new BusinessException("Cliente JURIDICO requiere identificacion tipo RUC");
        }

        if (request.nombres() != null || request.apellidos() != null) {
            throw new BusinessException("Cliente JURIDICO no debe tener nombres y apellidos");
        }

        if (request.fechaNacimiento() != null) {
            throw new BusinessException("Cliente JURIDICO no debe tener fecha de nacimiento");
        }
    }

    private void validarSubtipoCliente(Integer subtipoClienteId, TipoClienteEnum tipoCliente) {
        SubtipoCliente subtipo = subtipoClienteRepository.findById(subtipoClienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subtipo de cliente no encontrado: " + subtipoClienteId
                ));

        if (subtipo.getEstado() != EstadoCatalogoEnum.ACTIVO) {
            throw new BusinessException("El subtipo de cliente no esta activo");
        }

        if (subtipo.getTipoCliente() != tipoCliente) {
            throw new BusinessException("El subtipo de cliente no corresponde al tipo de cliente indicado");
        }
    }

    private Cliente obtenerRepresentanteLegal(ClienteRequest request) {
        if (request.representanteLegalId() == null) {
            return null;
        }

        Cliente representante = obtenerEntidad(request.representanteLegalId());

        if (request.tipoCliente() == TipoClienteEnum.JURIDICO &&
                representante.getTipoCliente() != TipoClienteEnum.NATURAL) {
            throw new BusinessException("El representante legal debe ser un cliente NATURAL");
        }

        if (request.tipoCliente() == TipoClienteEnum.JURIDICO &&
                representante.getEstado() != EstadoClienteEnum.ACTIVO) {
            throw new BusinessException("El representante legal debe estar ACTIVO");
        }

        return representante;
    }

    private String sanitizarJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}