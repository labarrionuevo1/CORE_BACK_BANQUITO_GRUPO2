package com.banquito.core.transactions.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.AccountNotActiveException;
import com.banquito.core.shared.exception.IdempotencyException;
import com.banquito.core.shared.exception.InsufficientFundsException;
import com.banquito.core.shared.exception.ResourceNotFoundException;
import com.banquito.core.shared.exception.ValidationException;
import com.banquito.core.transactions.dto.api.MovimientoCuentaResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.enums.EstadoTransaccionEnum;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.mapper.TransaccionMapper;
import com.banquito.core.transactions.model.TransaccionCuenta;
import com.banquito.core.transactions.model.TransaccionInstitucional;
import com.banquito.core.transactions.repository.SubtipoTransaccionRepository;
import com.banquito.core.transactions.repository.TransaccionCuentaRepository;
import com.banquito.core.transactions.repository.TransaccionInstitucionalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotorTransaccionalService {
    private final CuentaRepository cuentaRepository;
    private final SubtipoTransaccionRepository subtipoRepository;
    private final TransaccionCuentaRepository transaccionCuentaRepository;
    private final CuentaInstitucionalRepository cuentaInstitucionalRepository;
    private final TransaccionInstitucionalRepository transaccionInstitucionalRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        try {
            return ejecutarTransferenciaInterna(request);
        } catch (RuntimeException ex) {
            auditoriaService.registrarEventoNuevaTransaccion(
                    "TRANSACTIONS",
                    "TRANSFERENCIA_RECHAZADA",
                    "TRANSACCION_CUENTA",
                    request.uuidOperacion() != null ? request.uuidOperacion().toString() : "SIN_UUID",
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"cuentaOrigen\":\"" + request.cuentaOrigen() +
                            "\",\"cuentaDestino\":\"" + request.cuentaDestino() +
                            "\",\"monto\":" + request.monto() +
                            ",\"motivo\":\"" + ex.getMessage() + "\"}"
            );

            throw ex;
        }
    }

    private TransferenciaResponse ejecutarTransferenciaInterna(TransferenciaRequest request) {
        log.info("Ejecutando transferencia: {} -> {} por {}", request.cuentaOrigen(), request.cuentaDestino(), request.monto());

        Cuenta origen = cuentaRepository.findByNumeroCuenta(request.cuentaOrigen())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta origen no encontrada: " + request.cuentaOrigen()));

        Cuenta destino = cuentaRepository.findByNumeroCuenta(request.cuentaDestino())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada: " + request.cuentaDestino()));

        if (origen.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException("Cuenta origen no está activa: " + request.cuentaOrigen());
        }

        if (destino.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException("Cuenta destino no está activa para pago masivo: " + request.cuentaDestino());
        }

        BigDecimal saldoConSobregiro = origen.getSaldoDisponible().add(origen.getLimiteSobregiro());

        if (saldoConSobregiro.compareTo(request.monto()) < 0) {
            throw new InsufficientFundsException(
                    "Saldo insuficiente en cuenta " + request.cuentaOrigen() +
                            ". Disponible: " + origen.getSaldoDisponible() +
                            ", Monto: " + request.monto()
            );
        }

        var subtipo = subtipoRepository.findByCodigo(request.codigoSubtipo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subtipo de transacción no encontrado: " + request.codigoSubtipo()
                ));

        LocalDate fechaNegocio = LocalDate.now();

        if (transaccionCuentaRepository.existsByCuentaAndUuidTransaccionAndFechaNegocio(
                origen,
                request.uuidOperacion(),
                fechaNegocio
        )) {
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

        auditoriaService.registrarEvento(
                "TRANSACTIONS",
                "TRANSFERENCIA_EXITOSA",
                "TRANSACCION_CUENTA",
                uuidDebito.toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.SWITCH,
                "{\"cuentaOrigen\":\"" + request.cuentaOrigen() +
                        "\",\"cuentaDestino\":\"" + request.cuentaDestino() +
                        "\",\"monto\":" + request.monto() + "}"
        );

        log.info(
                "Transferencia completada exitosamente. Débito: {}, Crédito: {}, Monto: {}",
                uuidDebito,
                uuidCredito,
                request.monto()
        );

        return new TransferenciaResponse(
                "EXITOSA",
                uuidDebito,
                uuidCredito,
                grupo,
                origen.getSaldoDisponible()
        );
    }

    @Transactional
    public UUID debitarCuentaMatrizLiquidacion(
            String numeroCuenta,
            BigDecimal monto,
            UUID uuidGrupoOperacion,
            String referenciaExterna
    ) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta matriz no encontrada: " + numeroCuenta));

        if (cuenta.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException("Cuenta matriz no está activa: " + numeroCuenta);
        }

        BigDecimal limiteSobregiro = cuenta.getLimiteSobregiro() != null
                ? cuenta.getLimiteSobregiro()
                : BigDecimal.ZERO;

        BigDecimal saldoConSobregiro = cuenta.getSaldoDisponible().add(limiteSobregiro);

        if (saldoConSobregiro.compareTo(monto) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente en cuenta matriz " + numeroCuenta);
        }

        var subtipo = subtipoRepository.findByCodigo("COBRO_COMISION")
                .orElseThrow(() -> new ResourceNotFoundException("Subtipo de transacción no encontrado: COBRO_COMISION"));

        UUID uuid = UUID.randomUUID();

        cuenta.setSaldoContable(cuenta.getSaldoContable().subtract(monto));
        cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(monto));
        cuenta.setFechaUltimoMovimiento(LocalDateTime.now());

        cuentaRepository.save(cuenta);

        TransaccionCuenta transaccion = new TransaccionCuenta();
        transaccion.setCuenta(cuenta);
        transaccion.setSubtipoTransaccion(subtipo);
        transaccion.setUuidTransaccion(uuid);
        transaccion.setUuidGrupoOperacion(uuidGrupoOperacion);
        transaccion.setFechaNegocio(LocalDate.now());
        transaccion.setTipoMovimiento(TipoMovimientoEnum.DEBITO);
        transaccion.setMonto(monto);
        transaccion.setSaldoResultante(cuenta.getSaldoContable());
        transaccion.setEstado(EstadoTransaccionEnum.COMPLETADA);
        transaccion.setCanalOrigen(CanalOrigenEnum.SWITCH);
        transaccion.setReferenciaExterna(referenciaExterna);
        transaccion.setDescripcion("Débito de cuenta matriz por liquidación de comisión e IVA");

        transaccionCuentaRepository.save(transaccion);

        return uuid;
    }

    @Transactional
    public UUID registrarMovimientoInstitucional(String codigoCuenta, String codigoSubtipo, TipoMovimientoEnum tipo, BigDecimal monto, UUID grupo, String referencia) {
        var cuenta = cuentaInstitucionalRepository.findByCodigo(codigoCuenta).orElseThrow(() -> new ResourceNotFoundException("Cuenta institucional no encontrada"));
        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + codigoCuenta);
        }
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

    @Transactional(readOnly = true)
    public List<MovimientoCuentaResponse> obtenerMovimientosPorCuenta(String numeroCuenta) {
        return transaccionCuentaRepository.findUltimosMovimientosPorNumeroCuenta(numeroCuenta)
                .stream()
                .map(TransaccionMapper::toMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoCuentaResponse obtenerPorUuid(UUID uuid) {
        TransaccionCuenta transaccion = transaccionCuentaRepository.findByUuidTransaccion(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada: " + uuid));
        return TransaccionMapper.toMovimientoResponse(transaccion);
    }
    
}
