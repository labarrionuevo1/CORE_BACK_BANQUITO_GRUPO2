package com.banquito.core.integration.switchapi.service.impl;

import com.banquito.core.accounts.dto.response.SaldoCuentaResponse;
import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.integration.switchapi.dto.request.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.response.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.response.ValidarEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.service.IntegracionSwitchService;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.ValidationException;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import com.banquito.core.transactions.dto.response.TransferenciaResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.MotorTransaccionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegracionSwitchServiceImpl implements IntegracionSwitchService {

    private final ClienteRepository clienteRepository;
    private final CuentaService cuentaService;
    private final MotorTransaccionalService motorTransaccionalService;
    private final FeriadoService feriadoService;
    private final AuditoriaService auditoriaService;

    @Override
    @Transactional
    public ValidarEmpresaSwitchResponse validarEmpresa(String ruc) {
        var clienteOptional = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        if (clienteOptional.isEmpty()) {
            auditoriaService.registrarEvento(
                    "INTEGRATION_SWITCH",
                    "VALIDAR_EMPRESA",
                    "CLIENTE",
                    ruc,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"ruc\":\"" + ruc + "\",\"existe\":false,\"motivo\":\"NO_EXISTE\"}"
            );

            return new ValidarEmpresaSwitchResponse(
                    ruc,
                    false,
                    null,
                    false
            );
        }

        var cliente = clienteOptional.get();

        boolean esJuridico = cliente.getTipoCliente() == TipoClienteEnum.JURIDICO;
        boolean estaActivo = cliente.getEstado() == EstadoClienteEnum.ACTIVO;
        boolean activoPagosMasivos = Boolean.TRUE.equals(cliente.getActivoPagosMasivos());

        boolean habilitada = esJuridico && estaActivo && activoPagosMasivos;

        auditoriaService.registrarEvento(
                "INTEGRATION_SWITCH",
                "VALIDAR_EMPRESA",
                "CLIENTE",
                cliente.getId().toString(),
                habilitada ? ResultadoAuditoriaEnum.EXITOSO : ResultadoAuditoriaEnum.RECHAZADO,
                CanalOrigenEnum.SWITCH,
                "{\"ruc\":\"" + ruc +
                        "\",\"existe\":true" +
                        ",\"tipoCliente\":\"" + cliente.getTipoCliente() +
                        "\",\"estado\":\"" + cliente.getEstado() +
                        "\",\"activoPagosMasivos\":" + activoPagosMasivos +
                        ",\"habilitada\":" + habilitada + "}"
        );

        return new ValidarEmpresaSwitchResponse(
                ruc,
                true,
                cliente.getEstado().name(),
                activoPagosMasivos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoCuentaResponse consultarDisponibilidad(String numeroCuenta) {
        return CuentaMapper.toSaldoResponse(cuentaService.obtenerPorNumero(numeroCuenta));
    }

    @Override
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        return motorTransaccionalService.ejecutarTransferencia(request);
    }

    @Override
    @Transactional
    public LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request) {
        validarTotalLiquidacion(request);

        UUID uuidDebitoMatriz = motorTransaccionalService.debitarCuentaMatrizLiquidacion(
                request.cuentaMatriz(),
                request.totalDebitado(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        UUID uuidCreditoIngresos = motorTransaccionalService.registrarMovimientoInstitucional(
                request.codigoCuentaIngresos(),
                "INGRESO_SERVICIO_MASIVO",
                TipoMovimientoEnum.CREDITO,
                request.subtotalComision(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        UUID uuidCreditoIva = motorTransaccionalService.registrarMovimientoInstitucional(
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
                uuidDebitoMatriz,
                uuidCreditoIngresos,
                uuidCreditoIva,
                request.uuidGrupoOperacion()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoCuentaResponse validarCuentaDestino(String numeroCuenta) {
        var cuenta = cuentaService.obtenerPorNumero(numeroCuenta);

        if (cuenta.getEstado() != EstadoCuentaEnum.ACTIVA) {
            throw new ValidationException("La cuenta destino no esta activa: " + numeroCuenta);
        }

        return CuentaMapper.toSaldoResponse(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate siguienteDiaHabil(LocalDate fecha) {
        return feriadoService.calcularSiguienteDiaHabil(fecha);
    }

    private void validarTotalLiquidacion(LiquidacionServicioSwitchRequest request) {
        BigDecimal totalCalculado = request.subtotalComision().add(request.montoIva());

        if (totalCalculado.compareTo(request.totalDebitado()) != 0) {
            throw new ValidationException("El total debitado debe ser igual a subtotalComision + montoIva");
        }
    }
}