package com.banquito.core.accounts.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.accounts.dto.api.BloquearCuentaRequest;
import com.banquito.core.accounts.dto.api.CambiarEstadoCuentaRequest;
import com.banquito.core.accounts.dto.api.CrearCuentaRequest;
import com.banquito.core.accounts.dto.api.CuentaResponse;
import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.accounts.enums.EstadoBloqueoCuentaEnum;
import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.model.BloqueoCuenta;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.model.HistorialEstadoCuenta;
import com.banquito.core.accounts.repository.BloqueoCuentaRepository;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.accounts.repository.HistorialEstadoCuentaRepository;
import com.banquito.core.accounts.repository.SubtipoCuentaRepository;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.branches.repository.SucursalRepository;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.notifications.mapper.NotificacionMapper;
import com.banquito.core.notifications.service.NotificacionService;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.shared.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private static final String MODULO_ACCOUNTS = "ACCOUNTS";
    private static final String ENTIDAD_CUENTA = "CUENTA";
    private static final String ENTIDAD_BLOQUEO_CUENTA = "BLOQUEO_CUENTA";

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final SubtipoCuentaRepository subtipoCuentaRepository;
    private final BloqueoCuentaRepository bloqueoRepository;
    private final HistorialEstadoCuentaRepository historialRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;
    private final AuditoriaService auditoriaService;
    private final NotificacionService notificacionService;

    @Override
    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll().stream().map(CuentaMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public List<CuentaResponse> listarPorCliente(Integer clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));

        return cuentaRepository.findByClienteId(clienteId)
                .stream()
                .map(CuentaMapper::toResponse)
                .toList();
    }

    @Override
    public Cuenta obtenerEntidad(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + id));
    }

    @Override
    public Cuenta obtenerPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));
    }

    @Override
    public CuentaResponse obtenerResponsePorNumero(String numeroCuenta) {
        return CuentaMapper.toResponse(obtenerPorNumero(numeroCuenta));
    }

    @Override
    public CuentaResponse obtener(Integer id) {
        return CuentaMapper.toResponse(obtenerEntidad(id));
    }

    @Override
    public SaldoCuentaResponse saldo(String numeroCuenta) {
        return CuentaMapper.toSaldoResponse(obtenerPorNumero(numeroCuenta));
    }

    @Override
    @Transactional
    public CuentaResponse crear(CrearCuentaRequest request) {
        var cliente = clienteRepository.findById(request.clienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        var sucursal = sucursalRepository.findById(request.sucursalId()).orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        var subtipo = subtipoCuentaRepository.findById(request.subtipoCuentaId()).orElseThrow(() -> new ResourceNotFoundException("Subtipo de cuenta no encontrado"));
        String numero = sucursal.getCodigoSucursal() + String.format("%09d", System.currentTimeMillis() % 1000000000);
        Cuenta c = new Cuenta();
        c.setNumeroCuenta(numero);
        c.setCliente(cliente);
        c.setSucursal(sucursal);
        c.setSubtipoCuenta(subtipo);
        c.setSaldoContable(BigDecimal.ZERO);
        c.setSaldoDisponible(BigDecimal.ZERO);
        c.setPermiteSobregiro(Boolean.TRUE.equals(request.permiteSobregiro()));
        c.setEsFavoritaPagos(Boolean.TRUE.equals(request.esFavoritaPagos()));
        Cuenta cuentaGuardada = cuentaRepository.save(c);

        auditoriaService.registrarEvento(
                MODULO_ACCOUNTS,
                "CREAR_CUENTA",
                ENTIDAD_CUENTA,
                cuentaGuardada.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"numeroCuenta\":\"" + cuentaGuardada.getNumeroCuenta() + "\"}"
        );

        return CuentaMapper.toResponse(cuentaGuardada);
    }

    @Override
    @Transactional
    public CuentaResponse cambiarEstado(Integer id, CambiarEstadoCuentaRequest request) {
        Cuenta cuenta = obtenerEntidad(id);
        var anterior = cuenta.getEstado();
        cuenta.setEstado(request.nuevoEstado());
        var usuario = usuarioCoreRepository.findById(request.usuarioCoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario core no encontrado: " + request.usuarioCoreId()));
        HistorialEstadoCuenta historial = new HistorialEstadoCuenta();
        historial.setCuenta(cuenta);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(request.nuevoEstado());
        historial.setMotivoCambio(request.motivoCambio());
        historial.setUsuarioCore(usuario);
        historialRepository.save(historial);

        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);

        auditoriaService.registrarEvento(
                MODULO_ACCOUNTS,
                "CAMBIAR_ESTADO_CUENTA",
                ENTIDAD_CUENTA,
                cuentaGuardada.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"estadoAnterior\":\"" + anterior + "\",\"estadoNuevo\":\"" + request.nuevoEstado() + "\"}"
        );

        if (esEstadoCritico(request.nuevoEstado())) {
            notificacionService.enviarCorreo(
                    NotificacionMapper.toCambioEstadoCriticoRequest(
                            cuentaGuardada,
                            anterior,
                            request.nuevoEstado(),
                            request.motivoCambio()
                    ),
                    "CAMBIAR_ESTADO_CUENTA",
                    ENTIDAD_CUENTA,
                    cuentaGuardada.getId().toString(),
                    CanalOrigenEnum.CORE
            );
        }

        return CuentaMapper.toResponse(cuentaGuardada);
    }

    @Override
    @Transactional
    public void bloquear(Integer id, BloquearCuentaRequest request) {
        Cuenta cuenta = obtenerEntidad(id);

        if (cuenta.getSaldoDisponible().compareTo(request.montoBloqueado()) < 0) {
            throw new ValidationException("Saldo disponible insuficiente para bloquear el monto solicitado");
        }

        var usuario = usuarioCoreRepository.findById(request.usuarioCoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + request.usuarioCoreId()
                ));

        BloqueoCuenta bloqueo = new BloqueoCuenta();
        bloqueo.setCuenta(cuenta);
        bloqueo.setMontoBloqueado(request.montoBloqueado());
        bloqueo.setMotivo(request.motivo());
        bloqueo.setAutoridadOrdenante(request.autoridadOrdenante());
        bloqueo.setEstado(EstadoBloqueoCuentaEnum.ACTIVO);
        bloqueo.setUsuarioCore(usuario);
        bloqueo.setObservaciones(request.observaciones());

        cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(request.montoBloqueado()));

        bloqueoRepository.save(bloqueo);
        cuentaRepository.save(cuenta);

        auditoriaService.registrarEvento(
                MODULO_ACCOUNTS,
                "BLOQUEAR_CUENTA",
                ENTIDAD_CUENTA,
                cuenta.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"montoBloqueado\":" + request.montoBloqueado() + "}"
        );

        notificacionService.enviarCorreo(
                NotificacionMapper.toBloqueoCuentaRequest(cuenta, bloqueo),
                "BLOQUEAR_CUENTA",
                ENTIDAD_CUENTA,
                cuenta.getId().toString(),
                CanalOrigenEnum.CORE
        );
    }

    @Override
    @Transactional
    public void liberarBloqueo(Integer idBloqueo) {
        BloqueoCuenta bloqueo = bloqueoRepository.findById(idBloqueo)
                .orElseThrow(() -> new ResourceNotFoundException("Bloqueo no encontrado: " + idBloqueo));

        if (bloqueo.getEstado() != EstadoBloqueoCuentaEnum.ACTIVO) {
            throw new BusinessException("El bloqueo ya fue liberado o no esta activo");
        }

        Cuenta cuenta = bloqueo.getCuenta();

        bloqueo.setEstado(EstadoBloqueoCuentaEnum.LIBERADO);
        cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().add(bloqueo.getMontoBloqueado()));
        bloqueo.setFechaLiberacion(LocalDateTime.now());

        bloqueoRepository.save(bloqueo);
        cuentaRepository.save(cuenta);
        auditoriaService.registrarEvento(
                MODULO_ACCOUNTS,
                "LIBERAR_BLOQUEO_CUENTA",
                ENTIDAD_BLOQUEO_CUENTA,
                idBloqueo.toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.CORE,
                "{\"cuentaId\":\"" + cuenta.getId() + "\",\"montoLiberado\":" + bloqueo.getMontoBloqueado() + "}"
        );
    }

    private boolean esEstadoCritico(EstadoCuentaEnum estado) {
        return estado == EstadoCuentaEnum.BLOQUEADA
                || estado == EstadoCuentaEnum.SUSPENDIDA;
    }
}