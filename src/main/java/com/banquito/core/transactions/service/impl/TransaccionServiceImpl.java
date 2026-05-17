package com.banquito.core.transactions.service.impl;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.institutional.repository.CuentaInstitucionalRepository;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.enums.EstadoUsuarioCoreEnum;
import com.banquito.core.security.model.CredencialWeb;
import com.banquito.core.security.model.UsuarioCore;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.security.repository.UsuarioCoreRepository;
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
import com.banquito.core.transactions.model.SubtipoTransaccion;
import com.banquito.core.transactions.model.TransaccionCuenta;
import com.banquito.core.transactions.model.TransaccionInstitucional;
import com.banquito.core.transactions.repository.SubtipoTransaccionRepository;
import com.banquito.core.transactions.repository.TransaccionCuentaRepository;
import com.banquito.core.transactions.repository.TransaccionInstitucionalRepository;
import com.banquito.core.transactions.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private static final String MODULO_TRANSACCIONES = "TRANSACTIONS";
    private static final String ENTIDAD_TRANSACCION_CUENTA = "TRANSACCION_CUENTA";
    private static final String PREFIJO_COMPROBANTE = "BQ";

    private final CuentaRepository cuentaRepository;
    private final SubtipoTransaccionRepository subtipoRepository;
    private final TransaccionCuentaRepository transaccionCuentaRepository;
    private final CuentaInstitucionalRepository cuentaInstitucionalRepository;
    private final TransaccionInstitucionalRepository transaccionInstitucionalRepository;
    private final UsuarioCoreRepository usuarioCoreRepository;
    private final CredencialWebRepository credencialWebRepository;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        try {
            return ejecutarTransferenciaInterna(request);
        } catch (RuntimeException ex) {
            CanalOrigenEnum canalOrigen = resolverCanalOrigen(request);

            auditoriaService.registrarEventoNuevaTransaccion(
                    MODULO_TRANSACCIONES,
                    "TRANSFERENCIA_RECHAZADA",
                    ENTIDAD_TRANSACCION_CUENTA,
                    request.uuidOperacion() != null ? request.uuidOperacion().toString() : "SIN_UUID",
                    ResultadoAuditoriaEnum.RECHAZADO,
                    canalOrigen,
                    "{\"cuentaOrigen\":\"" + sanitizarJson(request.cuentaOrigen()) +
                            "\",\"cuentaDestino\":\"" + sanitizarJson(request.cuentaDestino()) +
                            "\",\"monto\":" + request.monto() +
                            ",\"motivo\":\"" + sanitizarJson(ex.getMessage()) + "\"}"
            );

            throw ex;
        }
    }

    private TransferenciaResponse ejecutarTransferenciaInterna(TransferenciaRequest request) {
        validarActorTransaccional(request);
        validarCuentasDiferentes(request);

        Cuenta origen = cuentaRepository.findByNumeroCuenta(request.cuentaOrigen())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta origen no encontrada: " + request.cuentaOrigen()
                ));

        Cuenta destino = cuentaRepository.findByNumeroCuenta(request.cuentaDestino())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta destino no encontrada: " + request.cuentaDestino()
                ));

        validarActorYPropiedad(request, origen);
        validarCuentasActivas(origen, destino, request);
        validarSaldoDisponible(origen, request.monto());

        SubtipoTransaccion subtipo = subtipoRepository.findByCodigo(request.codigoSubtipo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subtipo de transaccion no encontrado: " + request.codigoSubtipo()
                ));

        LocalDate fechaNegocio = resolverFechaNegocio(request);
        CanalOrigenEnum canalOrigen = resolverCanalOrigen(request);
        UsuarioCore usuarioCore = resolverUsuarioCore(request);
        CredencialWeb credencialWeb = resolverCredencialWeb(request);

        validarIdempotencia(origen, request.uuidOperacion(), fechaNegocio);

        UUID uuidGrupoOperacion = request.uuidGrupoOperacion() != null
                ? request.uuidGrupoOperacion()
                : UUID.randomUUID();

        UUID uuidDebito = request.uuidOperacion();
        UUID uuidCredito = UUID.randomUUID();
        String numeroComprobante = generarNumeroComprobante(fechaNegocio, uuidGrupoOperacion);

        origen.setSaldoContable(origen.getSaldoContable().subtract(request.monto()));
        origen.setSaldoDisponible(origen.getSaldoDisponible().subtract(request.monto()));
        origen.setFechaUltimoMovimiento(LocalDateTime.now());

        destino.setSaldoContable(destino.getSaldoContable().add(request.monto()));
        destino.setSaldoDisponible(destino.getSaldoDisponible().add(request.monto()));
        destino.setFechaUltimoMovimiento(LocalDateTime.now());

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        TransaccionCuenta transaccionDebito = construirTransaccionCuenta(
                origen,
                subtipo,
                uuidDebito,
                uuidGrupoOperacion,
                fechaNegocio,
                TipoMovimientoEnum.DEBITO,
                request.monto(),
                origen.getSaldoContable(),
                canalOrigen,
                request.referenciaExterna(),
                request.descripcion(),
                numeroComprobante,
                usuarioCore,
                credencialWeb
        );

        transaccionCuentaRepository.save(transaccionDebito);

        TransaccionCuenta transaccionCredito = construirTransaccionCuenta(
                destino,
                subtipo,
                uuidCredito,
                uuidGrupoOperacion,
                fechaNegocio,
                TipoMovimientoEnum.CREDITO,
                request.monto(),
                destino.getSaldoContable(),
                canalOrigen,
                request.referenciaExterna(),
                request.descripcion(),
                numeroComprobante,
                usuarioCore,
                credencialWeb
        );

        transaccionCuentaRepository.save(transaccionCredito);

        auditoriaService.registrarEvento(
                MODULO_TRANSACCIONES,
                "TRANSFERENCIA_EXITOSA",
                ENTIDAD_TRANSACCION_CUENTA,
                uuidDebito.toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                canalOrigen,
                "{\"cuentaOrigen\":\"" + sanitizarJson(request.cuentaOrigen()) +
                        "\",\"cuentaDestino\":\"" + sanitizarJson(request.cuentaDestino()) +
                        "\",\"monto\":" + request.monto() +
                        ",\"fechaNegocio\":\"" + fechaNegocio +
                        "\",\"numeroComprobante\":\"" + numeroComprobante + "\"}"
        );

        return TransaccionMapper.toTransferenciaResponse(
                uuidDebito,
                uuidCredito,
                uuidGrupoOperacion,
                origen.getSaldoDisponible(),
                numeroComprobante
        );
    }

    @Override
    @Transactional
    public UUID debitarCuentaMatrizLiquidacion(
            String numeroCuenta,
            BigDecimal monto,
            UUID uuidGrupoOperacion,
            String referenciaExterna,
            boolean permitirSobregiroLiquidacion
    ) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta matriz no encontrada: " + numeroCuenta
                ));

        if (cuenta.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException("Cuenta matriz no esta activa: " + numeroCuenta);
        }

        if (!permitirSobregiroLiquidacion) {
            validarSaldoDisponible(cuenta, monto);
        }

        SubtipoTransaccion subtipo = subtipoRepository.findByCodigo("COBRO_COMISION")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subtipo de transaccion no encontrado: COBRO_COMISION"
                ));

        UUID uuid = UUID.randomUUID();
        LocalDate fechaNegocio = LocalDate.now();
        String numeroComprobante = generarNumeroComprobante(fechaNegocio, uuidGrupoOperacion);

        cuenta.setSaldoContable(cuenta.getSaldoContable().subtract(monto));
        cuenta.setSaldoDisponible(cuenta.getSaldoDisponible().subtract(monto));
        cuenta.setFechaUltimoMovimiento(LocalDateTime.now());

        cuentaRepository.save(cuenta);

        TransaccionCuenta transaccion = construirTransaccionCuenta(
                cuenta,
                subtipo,
                uuid,
                uuidGrupoOperacion,
                fechaNegocio,
                TipoMovimientoEnum.DEBITO,
                monto,
                cuenta.getSaldoContable(),
                CanalOrigenEnum.SWITCH,
                referenciaExterna,
                "Debito de cuenta matriz por liquidacion de comision e IVA",
                numeroComprobante,
                null,
                null
        );

        transaccionCuentaRepository.save(transaccion);

        return uuid;
    }

    @Override
    @Transactional
    public UUID registrarMovimientoInstitucional(
            String codigoCuenta,
            String codigoSubtipo,
            TipoMovimientoEnum tipo,
            BigDecimal monto,
            UUID grupo,
            String referencia
    ) {
        var cuenta = cuentaInstitucionalRepository.findByCodigo(codigoCuenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta institucional no encontrada: " + codigoCuenta
                ));

        if (cuenta.getEstado() != EstadoCuentaInstitucionalEnum.ACTIVA) {
            throw new ValidationException("Cuenta institucional no activa: " + codigoCuenta);
        }

        SubtipoTransaccion subtipo = subtipoRepository.findByCodigo(codigoSubtipo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subtipo de transaccion no encontrado: " + codigoSubtipo
                ));

        UUID uuid = UUID.randomUUID();

        BigDecimal nuevoSaldo = tipo == TipoMovimientoEnum.CREDITO
                ? cuenta.getSaldoContable().add(monto)
                : cuenta.getSaldoContable().subtract(monto);

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

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCuentaResponse> obtenerMovimientosPorCuenta(String numeroCuenta) {
        return transaccionCuentaRepository.findUltimosMovimientosPorNumeroCuenta(numeroCuenta)
                .stream()
                .map(TransaccionMapper::toMovimientoResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCuentaResponse obtenerPorUuid(UUID uuid) {
        TransaccionCuenta transaccion = transaccionCuentaRepository.findByUuidTransaccion(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaccion no encontrada: " + uuid
                ));

        return TransaccionMapper.toMovimientoResponse(transaccion);
    }

    private void validarCuentasActivas(
            Cuenta origen,
            Cuenta destino,
            TransferenciaRequest request
    ) {
        if (origen.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException(
                    "Cuenta origen no esta activa: " + request.cuentaOrigen()
            );
        }

        if (destino.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new AccountNotActiveException(
                    "Cuenta destino no esta activa para pago masivo: " + request.cuentaDestino()
            );
        }
    }

    private void validarSaldoDisponible(Cuenta cuenta, BigDecimal monto) {
        BigDecimal limiteSobregiro = Boolean.TRUE.equals(cuenta.getPermiteSobregiro()) && cuenta.getLimiteSobregiro() != null
                ? cuenta.getLimiteSobregiro()
                : BigDecimal.ZERO;

        BigDecimal saldoConSobregiro = cuenta.getSaldoDisponible().add(limiteSobregiro);

        if (saldoConSobregiro.compareTo(monto) < 0) {
            throw new InsufficientFundsException(
                    "Saldo insuficiente en cuenta " + cuenta.getNumeroCuenta() +
                            ". Disponible: " + cuenta.getSaldoDisponible() +
                            ", Monto: " + monto
            );
        }
    }

    private void validarIdempotencia(
            Cuenta origen,
            UUID uuidOperacion,
            LocalDate fechaNegocio
    ) {
        if (transaccionCuentaRepository.existsByCuentaAndUuidTransaccionAndFechaNegocio(
                origen,
                uuidOperacion,
                fechaNegocio
        )) {
            throw new IdempotencyException(
                    "Transaccion duplicada: UUID ya existe para esta cuenta en la fecha de negocio"
            );
        }
    }

    private void validarActorYPropiedad(TransferenciaRequest request, Cuenta cuentaOrigen) {
        CanalOrigenEnum canal = resolverCanalOrigen(request);

        if (canal == CanalOrigenEnum.WEB) {
            if (request.credencialWebId() == null) {
                throw new ValidationException("Las operaciones WEB requieren credencial web");
            }

            var credencial = credencialWebRepository.findById(request.credencialWebId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Credencial web no encontrada: " + request.credencialWebId()
                    ));

            if (credencial.getEstado() != EstadoCredencialWebEnum.ACTIVO) {
                throw new ValidationException("La credencial web no esta activa");
            }

            if (credencial.getCliente() == null) {
                throw new ValidationException("La credencial web no tiene cliente asociado");
            }

            if (credencial.getCliente().getEstado() != EstadoClienteEnum.ACTIVO) {
                throw new ValidationException("El cliente asociado a la credencial no esta activo");
            }

            if (!cuentaOrigen.getCliente().getId().equals(credencial.getCliente().getId())) {
                throw new ValidationException("La cuenta origen no pertenece al cliente autenticado");
            }
        }

        if (canal == CanalOrigenEnum.CORE && request.usuarioCoreId() != null) {
            var usuario = usuarioCoreRepository.findById(request.usuarioCoreId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario core no encontrado: " + request.usuarioCoreId()
                    ));

            if (usuario.getEstado() != EstadoUsuarioCoreEnum.ACTIVO) {
                throw new ValidationException("El usuario core no esta activo");
            }
        }
    }

    private void validarActorTransaccional(TransferenciaRequest request) {
        if (request.usuarioCoreId() != null && request.credencialWebId() != null) {
            throw new ValidationException("La transaccion no puede tener usuarioCoreId y credencialWebId al mismo tiempo");
        }
    }

    private void validarCuentasDiferentes(TransferenciaRequest request) {
        if (request.cuentaOrigen() != null &&
                request.cuentaDestino() != null &&
                request.cuentaOrigen().equals(request.cuentaDestino())) {
            throw new ValidationException("La cuenta origen y la cuenta destino no pueden ser iguales");
        }
    }

    private CanalOrigenEnum resolverCanalOrigen(TransferenciaRequest request) {
        return request.canalOrigen() != null
                ? request.canalOrigen()
                : CanalOrigenEnum.CORE;
    }

    private LocalDate resolverFechaNegocio(TransferenciaRequest request) {
        return request.fechaNegocio() != null
                ? request.fechaNegocio()
                : LocalDate.now();
    }

    private UsuarioCore resolverUsuarioCore(TransferenciaRequest request) {
        if (request.usuarioCoreId() == null) {
            return null;
        }

        return usuarioCoreRepository.findById(request.usuarioCoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario core no encontrado: " + request.usuarioCoreId()
                ));
    }

    private CredencialWeb resolverCredencialWeb(TransferenciaRequest request) {
        if (request.credencialWebId() == null) {
            return null;
        }

        return credencialWebRepository.findById(request.credencialWebId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credencial web no encontrada: " + request.credencialWebId()
                ));
    }

    private TransaccionCuenta construirTransaccionCuenta(
            Cuenta cuenta,
            SubtipoTransaccion subtipo,
            UUID uuidTransaccion,
            UUID uuidGrupoOperacion,
            LocalDate fechaNegocio,
            TipoMovimientoEnum tipoMovimiento,
            BigDecimal monto,
            BigDecimal saldoResultante,
            CanalOrigenEnum canalOrigen,
            String referenciaExterna,
            String descripcion,
            String numeroComprobante,
            UsuarioCore usuarioCore,
            CredencialWeb credencialWeb
    ) {
        TransaccionCuenta transaccion = new TransaccionCuenta();
        transaccion.setCuenta(cuenta);
        transaccion.setSubtipoTransaccion(subtipo);
        transaccion.setUuidTransaccion(uuidTransaccion);
        transaccion.setUuidGrupoOperacion(uuidGrupoOperacion);
        transaccion.setFechaNegocio(fechaNegocio);
        transaccion.setTipoMovimiento(tipoMovimiento);
        transaccion.setMonto(monto);
        transaccion.setSaldoResultante(saldoResultante);
        transaccion.setEstado(EstadoTransaccionEnum.COMPLETADA);
        transaccion.setCanalOrigen(canalOrigen);
        transaccion.setReferenciaExterna(referenciaExterna);
        transaccion.setDescripcion(descripcion);
        transaccion.setNumeroComprobante(numeroComprobante);
        transaccion.setUsuarioCore(usuarioCore);
        transaccion.setCredencialWeb(credencialWeb);

        return transaccion;
    }

    private String generarNumeroComprobante(LocalDate fechaNegocio, UUID uuidGrupoOperacion) {
        String fecha = fechaNegocio.format(DateTimeFormatter.BASIC_ISO_DATE);
        String sufijo = uuidGrupoOperacion.toString().replace("-", "").substring(0, 8).toUpperCase();
        return PREFIJO_COMPROBANTE + "-" + fecha + "-" + sufijo;
    }

    private String sanitizarJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}