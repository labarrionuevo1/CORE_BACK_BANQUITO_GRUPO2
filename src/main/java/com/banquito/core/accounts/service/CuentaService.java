package com.banquito.core.accounts.service;

import com.banquito.core.accounts.dto.request.BloquearCuentaRequest;
import com.banquito.core.accounts.dto.request.CambiarEstadoCuentaRequest;
import com.banquito.core.accounts.dto.request.CrearCuentaRequest;
import com.banquito.core.accounts.dto.response.CuentaResponse;
import com.banquito.core.accounts.dto.response.SaldoCuentaResponse;
import com.banquito.core.accounts.enums.EstadoBloqueoCuentaEnum;
import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.model.BloqueoCuenta;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.model.HistorialEstadoCuenta;
import com.banquito.core.accounts.repository.BloqueoCuentaRepository;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.accounts.repository.HistorialEstadoCuentaRepository;
import com.banquito.core.accounts.repository.SubtipoCuentaRepository;
import com.banquito.core.branches.repository.SucursalRepository;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.security.repository.UsuarioCoreRepository;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {
    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final SubtipoCuentaRepository subtipoCuentaRepository;
    private final BloqueoCuentaRepository bloqueoRepository;
    private final HistorialEstadoCuentaRepository historialRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;

    public List<CuentaResponse> listar() { return cuentaRepository.findAll().stream().map(CuentaMapper::toResponse).toList(); }
    public Cuenta obtenerEntidad(Integer id) { return cuentaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + id)); }
    public Cuenta obtenerPorNumero(String numeroCuenta) { return cuentaRepository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta)); }
    public CuentaResponse obtener(Integer id) { return CuentaMapper.toResponse(obtenerEntidad(id)); }
    public SaldoCuentaResponse saldo(String numeroCuenta) { return CuentaMapper.toSaldoResponse(obtenerPorNumero(numeroCuenta)); }

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
        return CuentaMapper.toResponse(cuentaRepository.save(c));
    }

    @Transactional
    public CuentaResponse cambiarEstado(Integer id, CambiarEstadoCuentaRequest request) {
        Cuenta cuenta = obtenerEntidad(id);
        var anterior = cuenta.getEstado();
        cuenta.setEstado(request.nuevoEstado());
        var usuario = request.usuarioCoreId() == null ? null : usuarioCoreRepository.findById(request.usuarioCoreId()).orElse(null);
        HistorialEstadoCuenta historial = new HistorialEstadoCuenta();
        historial.setCuenta(cuenta);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(request.nuevoEstado());
        historial.setMotivoCambio(request.motivoCambio());
        historial.setUsuarioCore(usuario);
        historialRepository.save(historial);
        return CuentaMapper.toResponse(cuentaRepository.save(cuenta));
    }

    @Transactional
    public void bloquear(Integer id, BloquearCuentaRequest request) {
        Cuenta cuenta = obtenerEntidad(id);
        var usuario = request.usuarioCoreId() == null ? null : usuarioCoreRepository.findById(request.usuarioCoreId()).orElse(null);
        BloqueoCuenta bloqueo = new BloqueoCuenta();
        bloqueo.setCuenta(cuenta);
        bloqueo.setMontoBloqueado(request.montoBloqueado());
        bloqueo.setMotivo(request.motivo());
        bloqueo.setAutoridadOrdenante(request.autoridadOrdenante());
        bloqueo.setEstado(EstadoBloqueoCuentaEnum.ACTIVO);
        bloqueo.setUsuarioCore(usuario);
        bloqueo.setObservaciones(request.observaciones());
        bloqueoRepository.save(bloqueo);
        cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(request.montoBloqueado()));
        cuentaRepository.save(cuenta);
    }
}
