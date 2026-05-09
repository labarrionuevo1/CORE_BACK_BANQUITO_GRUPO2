package com.banquito.core.integration.switchapi.service;

import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.integration.switchapi.dto.request.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.response.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.response.ValidarEmpresaSwitchResponse;
import com.banquito.core.transactions.dto.request.TransferenciaRequest;
import com.banquito.core.transactions.dto.response.TransferenciaResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.MotorTransaccionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegracionSwitchService {
    private final ClienteRepository clienteRepository;
    private final CuentaService cuentaService;
    private final MotorTransaccionalService motor;

    public ValidarEmpresaSwitchResponse validarEmpresa(String ruc) {
        var cliente = clienteRepository.findByTipoIdentificacionAndIdentificacion(TipoIdentificacionEnum.RUC, ruc);
        return cliente.map(c -> new ValidarEmpresaSwitchResponse(ruc, true, c.getEstado().name(), c.getActivoPagosMasivos()))
                .orElseGet(() -> new ValidarEmpresaSwitchResponse(ruc, false, null, false));
    }

    public Object consultarDisponibilidad(String numeroCuenta) {
        return CuentaMapper.toSaldoResponse(cuentaService.obtenerPorNumero(numeroCuenta));
    }

    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        return motor.ejecutarTransferencia(request);
    }

    @Transactional
    public LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request) {
        UUID uuidDebito = UUID.randomUUID();
        // La liquidacion real debe debitar la cuenta matriz y acreditar cuentas institucionales.
        // Este contrato deja el flujo base y registra los creditos institucionales.
        UUID ingresos = motor.registrarMovimientoInstitucional(request.codigoCuentaIngresos(), "INGRESO_SERVICIO_MASIVO", TipoMovimientoEnum.CREDITO,
                request.subtotalComision(), request.uuidGrupoOperacion(), request.referenciaExterna());
        UUID iva = motor.registrarMovimientoInstitucional(request.codigoCuentaIva(), "IVA_POR_PAGAR", TipoMovimientoEnum.CREDITO,
                request.montoIva(), request.uuidGrupoOperacion(), request.referenciaExterna());
        return new LiquidacionServicioSwitchResponse("APLICADA", uuidDebito, ingresos, iva, request.uuidGrupoOperacion());
    }
}
