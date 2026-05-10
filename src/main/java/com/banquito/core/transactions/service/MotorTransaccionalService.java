package com.banquito.core.transactions.service;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.*;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MotorTransaccionalService {
    private final CuentaRepository cuentaRepository;
    private final SubtipoTransaccionRepository subtipoRepository;
    private final TransaccionCuentaRepository transaccionCuentaRepository;
    private final CuentaInstitucionalRepository cuentaInstitucionalRepository;
    private final TransaccionInstitucionalRepository transaccionInstitucionalRepository;

    @Transactional
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        log.info("Ejecutando transferencia: {} -> {} por {}", request.cuentaOrigen(), request.cuentaDestino(), request.monto());
        
        Cuenta origen = cuentaRepository.findByNumeroCuenta(request.cuentaOrigen())
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta origen no encontrada: " + request.cuentaOrigen()));
        Cuenta destino = cuentaRepository.findByNumeroCuenta(request.cuentaDestino())
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada: " + request.cuentaDestino()));
            
        if (origen.getEstado() != EstadoCuentaEnum.ACTIVA) 
            throw new AccountNotActiveException("Cuenta origen no está activa: " + request.cuentaOrigen());
        if (destino.getEstado() != EstadoCuentaEnum.ACTIVA) 
            throw new AccountNotActiveException("Cuenta destino no está activa para pago masivo: " + request.cuentaDestino());
            
        BigDecimal saldoConSobregiro = origen.getSaldoDisponible().add(origen.getLimiteSobregiro());
        if (saldoConSobregiro.compareTo(request.monto()) < 0) 
            throw new InsufficientFundsException("Saldo insuficiente en cuenta " + request.cuentaOrigen() + 
                ". Disponible: " + origen.getSaldoDisponible() + ", Monto: " + request.monto());
                
        var subtipo = subtipoRepository.findByCodigo(request.codigoSubtipo())
            .orElseThrow(() -> new ResourceNotFoundException("Subtipo de transacción no encontrado: " + request.codigoSubtipo()));
        
        // RF-06: Validación de duplicidad UUID
        LocalDate fechaNegocio = LocalDate.now();
        if (transaccionCuentaRepository.existsByCuentaAndUuidTransaccionAndFechaNegocio(origen, request.uuidOperacion(), fechaNegocio)) {
            throw new IdempotencyException("Transacción duplicada: UUID ya existe para esta cuenta en la fecha de negocio");
        }
        
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
        
        // Registrar transacción de débito
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

        // Registrar transacción de crédito
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
        
        log.info("Transferencia completada exitosamente. Débito: {}, Crédito: {}, Monto: {}", 
            uuidDebito, uuidCredito, request.monto());
            
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
