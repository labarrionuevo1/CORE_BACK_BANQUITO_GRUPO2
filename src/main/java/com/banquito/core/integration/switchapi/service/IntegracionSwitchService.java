package com.banquito.core.integration.switchapi.service;

import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.MotorTransaccionalService;
import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.exception.ValidationException;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class IntegracionSwitchService {
    private final ClienteRepository clienteRepository;
    private final CuentaService cuentaService;
    private final MotorTransaccionalService motor;
    private final FeriadoService feriadoService;
    private final AuditoriaService auditoriaService;

    public ValidarEmpresaSwitchResponse validarEmpresa(String ruc) {
        var cliente = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        if (cliente.isEmpty()) {

            auditoriaService.registrarEvento(
                    "INTEGRATION_SWITCH",
                    "VALIDAR_EMPRESA",
                    "CLIENTE",
                    ruc,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"ruc\":\"" + ruc + "\",\"habilitada\":false,\"motivo\":\"NO_EXISTE\"}"
            );

            return new ValidarEmpresaSwitchResponse(ruc, false, null, false);
        }

        var c = cliente.get();

        boolean habilitada =
                c.getTipoCliente() == TipoClienteEnum.JURIDICO
                        && c.getEstado() == EstadoClienteEnum.ACTIVO
                        && Boolean.TRUE.equals(c.getActivoPagosMasivos());

        auditoriaService.registrarEvento(
                "INTEGRATION_SWITCH",
                "VALIDAR_EMPRESA",
                "CLIENTE",
                c.getId().toString(),
                habilitada ? ResultadoAuditoriaEnum.EXITOSO : ResultadoAuditoriaEnum.RECHAZADO,
                CanalOrigenEnum.SWITCH,
                "{\"ruc\":\"" + ruc + "\",\"habilitada\":" + habilitada + "}"
        );

        return new ValidarEmpresaSwitchResponse(
                ruc,
                habilitada,
                c.getEstado().name(),
                Boolean.TRUE.equals(c.getActivoPagosMasivos())
        );
    }

    public Object consultarDisponibilidad(String numeroCuenta) {
        return CuentaMapper.toSaldoResponse(cuentaService.obtenerPorNumero(numeroCuenta));
    }

    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        return motor.ejecutarTransferencia(request);
    }

    @Transactional
    public LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request) {

        BigDecimal totalCalculado = request.subtotalComision().add(request.montoIva());

        if (totalCalculado.compareTo(request.totalDebitado()) != 0) {
        throw new ValidationException("El total debitado debe ser igual a subtotalComision + montoIva");
        }

        UUID uuidDebito = motor.debitarCuentaMatrizLiquidacion(
                request.cuentaMatriz(),
                request.totalDebitado(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        UUID ingresos = motor.registrarMovimientoInstitucional(
                request.codigoCuentaIngresos(),
                "INGRESO_SERVICIO_MASIVO",
                TipoMovimientoEnum.CREDITO,
                request.subtotalComision(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        UUID iva = motor.registrarMovimientoInstitucional(
                request.codigoCuentaIva(),
                "IVA_SERVICIO_MASIVO",
                TipoMovimientoEnum.CREDITO,
                request.montoIva(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        auditoriaService.registrarEvento(
                "INTEGRATION_SWITCH",
                "LIQUIDAR_COMISION_IVA",
                "LIQUIDACION_SERVICIO",
                request.uuidGrupoOperacion().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.SWITCH,
                "{\"cuentaMatriz\":\"" + request.cuentaMatriz() +
                        "\",\"subtotalComision\":" + request.subtotalComision() +
                        ",\"montoIva\":" + request.montoIva() +
                        ",\"totalDebitado\":" + request.totalDebitado() + "}"
        );

        return new LiquidacionServicioSwitchResponse(
                "APLICADA",
                uuidDebito,
                ingresos,
                iva,
                request.uuidGrupoOperacion()
        );
    }

    public Object validarCuentaDestino(String numeroCuenta) {
        var cuenta = cuentaService.obtenerPorNumero(numeroCuenta);

        if (cuenta.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new ValidationException("La cuenta destino no está activa: " + numeroCuenta);
        }

        return CuentaMapper.toSaldoResponse(cuenta);
    }

    public LocalDate siguienteDiaHabil(LocalDate fecha) {
        return feriadoService.calcularSiguienteDiaHabil(fecha);
    }
}
