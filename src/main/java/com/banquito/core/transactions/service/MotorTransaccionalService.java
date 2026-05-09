package com.banquito.core.transactions.service;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.BusinessException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import com.banquito.core.transactions.dto.response.TransferenciaResponse;
import com.banquito.core.transactions.enums.EstadoTransaccionEnum;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.model.TransaccionCuenta;
import com.banquito.core.transactions.model.TransaccionInstitucional;
import com.banquito.core.transactions.repository.SubtipoTransaccionRepository;
import com.banquito.core.transactions.repository.TransaccionCuentaRepository;
import com.banquito.core.transactions.repository.TransaccionInstitucionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MotorTransaccionalService {
    private final CuentaRepository cuentaRepository;
    private final SubtipoTransaccionRepository subtipoRepository;
    private final TransaccionCuentaRepository transaccionCuentaRepository;
    private final CuentaInstitucionalRepository cuentaInstitucionalRepository;
    private final TransaccionInstitucionalRepository transaccionInstitucionalRepository;

    @Transactional
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        Cuenta origen = cuentaRepository.findByNumeroCuenta(request.cuentaOrigen()).orElseThrow(() -> new ResourceNotFoundException("Cuenta origen no encontrada"));
        Cuenta destino = cuentaRepository.findByNumeroCuenta(request.cuentaDestino()).orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada"));
        if (origen.getEstado() != EstadoCuentaEnum.ACTIVA) throw new BusinessException("Cuenta origen no activa");
        if (destino.getEstado() != EstadoCuentaEnum.ACTIVA) throw new BusinessException("Cuenta destino no activa para pago masivo");
        BigDecimal saldoConSobregiro = origen.getSaldoDisponible().add(origen.getLimiteSobregiro());
        if (saldoConSobregiro.compareTo(request.monto()) < 0) throw new BusinessException("Saldo insuficiente");
        var subtipo = subtipoRepository.findByCodigo(request.codigoSubtipo()).orElseThrow(() -> new ResourceNotFoundException("Subtipo de transaccion no encontrado"));
        UUID grupo = request.uuidGrupoOperacion() != null ? request.uuidGrupoOperacion() : UUID.randomUUID();
        UUID uuidDebito = request.uuidOperacion();
        UUID uuidCredito = UUID.randomUUID();

        origen.setSaldoContable(origen.getSaldoContable().subtract(request.monto()));
        origen.setSaldoDisponible(origen.getSaldoDisponible().subtract(request.monto()));
        origen.setFechaUltimoMovimiento(LocalDateTime.now());
        destino.setSaldoContable(destino.getSaldoContable().add(request.monto()));
        destino.setSaldoDisponible(destino.getSaldoDisponible().add(request.monto()));
        destino.setFechaUltimoMovimiento(LocalDateTime.now());
        cuentaRepository.save(origen);
        cuentaRepository.save(destino);
        LocalDate fechaNegocio = LocalDate.now();
        TransaccionCuenta transaccionDebito = new TransaccionCuenta();
        transaccionDebito.setCuenta(origen);
        transaccionDebito.setSubtipoTransaccion(subtipo);
        transaccionDebito.setUuidTransaccion(uuidDebito);
        transaccionDebito.setUuidGrupoOperacion(grupo);
        transaccionDebito.setFechaNegocio(fechaNegocio);
        transaccionDebito.setTipoMovimiento(TipoMovimientoEnum.DEBITO);
        transaccionDebito.setMonto(request.monto());
        transaccionDebito.setSaldoResultante(origen.getSaldoContable());
        transaccionDebito.setEstado(EstadoTransaccionEnum.COMPLETADA);
        transaccionDebito.setCanalOrigen(CanalOrigenEnum.SWITCH);
        transaccionDebito.setReferenciaExterna(request.referenciaExterna());
        transaccionDebito.setDescripcion(request.descripcion());
        transaccionCuentaRepository.save(transaccionDebito);

        TransaccionCuenta transaccionCredito = new TransaccionCuenta();
        transaccionCredito.setCuenta(destino);
        transaccionCredito.setSubtipoTransaccion(subtipo);
        transaccionCredito.setUuidTransaccion(uuidCredito);
        transaccionCredito.setUuidGrupoOperacion(grupo);
        transaccionCredito.setFechaNegocio(fechaNegocio);
        transaccionCredito.setTipoMovimiento(TipoMovimientoEnum.CREDITO);
        transaccionCredito.setMonto(request.monto());
        transaccionCredito.setSaldoResultante(destino.getSaldoContable());
        transaccionCredito.setEstado(EstadoTransaccionEnum.COMPLETADA);
        transaccionCredito.setCanalOrigen(CanalOrigenEnum.SWITCH);
        transaccionCredito.setReferenciaExterna(request.referenciaExterna());
        transaccionCredito.setDescripcion(request.descripcion());
        transaccionCuentaRepository.save(transaccionCredito);
        return new TransferenciaResponse("EXITOSA", uuidDebito, uuidCredito, grupo, origen.getSaldoDisponible());
    }

    @Transactional
    public UUID registrarMovimientoInstitucional(String codigoCuenta, String codigoSubtipo, TipoMovimientoEnum tipo, BigDecimal monto, UUID grupo, String referencia) {
        var cuenta = cuentaInstitucionalRepository.findByCodigo(codigoCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada"));
        var subtipo = subtipoRepository.findByCodigo(codigoSubtipo).orElseThrow(() -> new ResourceNotFoundException("Subtipo de transaccion no encontrado"));
        UUID uuid = UUID.randomUUID();
        BigDecimal nuevoSaldo = tipo == TipoMovimientoEnum.CREDITO ? cuenta.getSaldoContable().add(monto) : cuenta.getSaldoContable().subtract(monto);
        cuenta.setSaldoContable(nuevoSaldo);
        cuentaInstitucionalRepository.save(cuenta);
        TransaccionInstitucional transaccion = new TransaccionInstitucional();
        transaccion.setCuentaInstitucional(cuenta);
        transaccion.setSubtipoTransaccion(subtipo);
        transaccion.setUuidTransaccion(uuid);
        transaccion.setUuidGrupoOperacion(grupo);
        transaccion.setFechaNegocio(LocalDate.now());
        transaccion.setTipoMovimiento(tipo);
        transaccion.setMonto(monto);
        transaccion.setSaldoResultante(nuevoSaldo);
        transaccion.setEstado(EstadoTransaccionEnum.COMPLETADA);
        transaccion.setCanalOrigen(CanalOrigenEnum.SWITCH);
        transaccion.setReferenciaExterna(referencia);
        transaccionInstitucionalRepository.save(transaccion);
        return uuid;
    }
}
