package com.banquito.core.integration.switchapi.service.impl;

import com.banquito.core.accounts.dto.api.SaldoCuentaResponse;
import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.mapper.CuentaMapper;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.accounts.repository.CuentaRepository;
import com.banquito.core.accounts.service.CuentaService;
import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.audit.service.AuditoriaService;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.customers.repository.ClienteRepository;
import com.banquito.core.integration.switchapi.dto.api.CuentaFavoritaPagosResponse;
import com.banquito.core.integration.switchapi.dto.api.DiaHabilSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.LoginRequest;
import com.banquito.core.integration.switchapi.dto.api.LoginResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchRequest;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCredencialEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaDestinoSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaMatrizSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.mapper.IntegracionSwitchMapper;
import com.banquito.core.integration.switchapi.service.IntegracionSwitchService;
import com.banquito.core.parameters.repository.FeriadoRepository;
import com.banquito.core.parameters.service.FeriadoService;
import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.repository.CredencialWebRepository;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import com.banquito.core.shared.exception.ValidationException;
import com.banquito.core.transactions.dto.api.TransferenciaRequest;
import com.banquito.core.transactions.dto.api.TransferenciaResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegracionSwitchServiceImpl implements IntegracionSwitchService {

    private static final String MODULO_INTEGRACION_SWITCH = "INTEGRATION_SWITCH";
    private static final String ACCION_VALIDAR_EMPRESA = "VALIDAR_EMPRESA";
    private static final String ACCION_CONSULTAR_CUENTA_FAVORITA = "CONSULTAR_CUENTA_FAVORITA_PAGOS";
    private static final String ACCION_LOGIN_SWITCH = "LOGIN_SWITCH";
    private static final String ACCION_LIQUIDAR_COMISION_IVA = "LIQUIDAR_COMISION_IVA";

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final CuentaService cuentaService;
    private final CredencialWebRepository credencialWebRepository;
    private final TransaccionService transaccionService;
    private final FeriadoRepository feriadoRepository;
    private final FeriadoService feriadoService;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ValidarEmpresaSwitchResponse validarEmpresa(String ruc) {
        var clienteOptional = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        if (clienteOptional.isEmpty()) {
            auditoriaService.registrarEvento(
                    MODULO_INTEGRACION_SWITCH,
                    ACCION_VALIDAR_EMPRESA,
                    "CLIENTE",
                    ruc,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"ruc\":\"" + sanitizarJson(ruc) +
                            "\",\"existe\":false,\"habilitada\":false,\"motivo\":\"NO_EXISTE\"}"
            );

            return IntegracionSwitchMapper.toEmpresaNoExisteResponse(ruc);
        }

        Cliente cliente = clienteOptional.get();

        boolean esJuridico = cliente.getTipoCliente() == TipoClienteEnum.JURIDICO;
        boolean estaActivo = cliente.getEstado() == EstadoClienteEnum.ACTIVO;
        boolean activoPagosMasivos = Boolean.TRUE.equals(cliente.getActivoPagosMasivos());
        boolean credencialWebValida = credencialWebRepository.existsByClienteIdAndEstado(
                cliente.getId(),
                EstadoCredencialWebEnum.ACTIVO
        );
        boolean habilitada = esJuridico && estaActivo && activoPagosMasivos;

        auditoriaService.registrarEvento(
                MODULO_INTEGRACION_SWITCH,
                ACCION_VALIDAR_EMPRESA,
                "CLIENTE",
                cliente.getId().toString(),
                habilitada ? ResultadoAuditoriaEnum.EXITOSO : ResultadoAuditoriaEnum.RECHAZADO,
                CanalOrigenEnum.SWITCH,
                "{\"ruc\":\"" + sanitizarJson(ruc) +
                        "\",\"existe\":true" +
                        ",\"tipoCliente\":\"" + cliente.getTipoCliente() +
                        "\",\"estado\":\"" + cliente.getEstado() +
                        "\",\"activoPagosMasivos\":" + activoPagosMasivos +
                        ",\"credencialWebValida\":" + credencialWebValida +
                        ",\"habilitada\":" + habilitada + "}"
        );

        return IntegracionSwitchMapper.toEmpresaResponse(
                ruc,
                cliente,
                credencialWebValida,
                habilitada
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ValidarCuentaMatrizSwitchResponse validarCuentaMatriz(String ruc, String numeroCuenta) {
        var clienteOptional = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        var cuentaOptional = cuentaRepository.findByNumeroCuenta(numeroCuenta);

        if (clienteOptional.isEmpty()) {
            return IntegracionSwitchMapper.toCuentaMatrizEmpresaNoExisteResponse(
                    ruc,
                    numeroCuenta,
                    cuentaOptional.orElse(null)
            );
        }

        if (cuentaOptional.isEmpty()) {
            return IntegracionSwitchMapper.toCuentaMatrizNoExisteResponse(ruc, numeroCuenta);
        }

        Cliente empresa = clienteOptional.get();
        Cuenta cuenta = cuentaOptional.get();

        boolean perteneceEmpresa = cuenta.getCliente() != null
                && cuenta.getCliente().getId().equals(empresa.getId());

        boolean empresaHabilitada = empresa.getTipoCliente() == TipoClienteEnum.JURIDICO
                && empresa.getEstado() == EstadoClienteEnum.ACTIVO
                && Boolean.TRUE.equals(empresa.getActivoPagosMasivos());

        boolean permiteDebito = cuenta.getEstado() == EstadoCuentaEnum.ACTIVA;
        boolean valida = perteneceEmpresa && empresaHabilitada && permiteDebito;

        return IntegracionSwitchMapper.toCuentaMatrizResponse(
                ruc,
                cuenta,
                perteneceEmpresa,
                permiteDebito,
                valida
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ValidarCuentaDestinoSwitchResponse validarCuentaDestino(
            String numeroCuenta,
            String identificacionBeneficiario
    ) {
        var cuentaOptional = cuentaRepository.findByNumeroCuenta(numeroCuenta);

        if (cuentaOptional.isEmpty()) {
            return IntegracionSwitchMapper.toCuentaDestinoNoExisteResponse(
                    numeroCuenta,
                    identificacionBeneficiario
            );
        }

        Cuenta cuenta = cuentaOptional.get();

        boolean perteneceBeneficiario = false;
        if (identificacionBeneficiario != null && !identificacionBeneficiario.isBlank()) {
            perteneceBeneficiario = cuenta.getCliente() != null
                    && identificacionBeneficiario.equals(cuenta.getCliente().getIdentificacion());
        }

        boolean bloqueada = cuenta.getEstado() == EstadoCuentaEnum.BLOQUEADA;
        boolean permiteDeposito = IntegracionSwitchMapper.estadoPermiteDeposito(cuenta.getEstado());
        boolean valida = permiteDeposito && (
                identificacionBeneficiario == null
                        || identificacionBeneficiario.isBlank()
                        || perteneceBeneficiario
        );

        return IntegracionSwitchMapper.toCuentaDestinoResponse(
                identificacionBeneficiario,
                cuenta,
                perteneceBeneficiario,
                permiteDeposito,
                bloqueada,
                valida
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ValidarCredencialEmpresaSwitchResponse validarCredencialEmpresarial(
            String ruc,
            String username
    ) {
        var clienteOptional = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        if (clienteOptional.isEmpty()) {
            return IntegracionSwitchMapper.toCredencialEmpresaNoExisteResponse(ruc, username);
        }

        Cliente empresa = clienteOptional.get();
        var credencialOptional = credencialWebRepository.findByUsuario(username);

        if (credencialOptional.isEmpty()) {
            return IntegracionSwitchMapper.toCredencialNoExisteResponse(ruc, username);
        }

        var credencial = credencialOptional.get();

        boolean perteneceEmpresa = credencial.getCliente() != null
                && credencial.getCliente().getId().equals(empresa.getId());

        boolean valida = perteneceEmpresa
                && credencial.getEstado() == EstadoCredencialWebEnum.ACTIVO
                && empresa.getTipoCliente() == TipoClienteEnum.JURIDICO
                && empresa.getEstado() == EstadoClienteEnum.ACTIVO
                && Boolean.TRUE.equals(empresa.getActivoPagosMasivos());

        return IntegracionSwitchMapper.toCredencialEmpresaResponse(
                ruc,
                username,
                credencial,
                perteneceEmpresa,
                valida
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoCuentaResponse consultarDisponibilidad(String numeroCuenta) {
        return CuentaMapper.toSaldoResponse(
                cuentaService.obtenerPorNumero(numeroCuenta)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaFavoritaPagosResponse consultarCuentaFavoritaPagos(String ruc) {
        var clienteOptional = clienteRepository.findByTipoIdentificacionAndIdentificacion(
                TipoIdentificacionEnum.RUC,
                ruc
        );

        if (clienteOptional.isEmpty()) {
            auditoriaService.registrarEvento(
                    MODULO_INTEGRACION_SWITCH,
                    ACCION_CONSULTAR_CUENTA_FAVORITA,
                    "CLIENTE",
                    ruc,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"rucEmpresa\":\"" + sanitizarJson(ruc) + "\",\"existe\":false,\"motivo\":\"NO_EXISTE\"}"
            );

            return IntegracionSwitchMapper.toCuentaFavoritaNoExisteResponse(ruc);
        }

        var cliente = clienteOptional.get();
        var cuentaFavoritaOptional = cuentaRepository.findFirstByClienteIdAndEsFavoritaPagosTrue(cliente.getId());

        if (cuentaFavoritaOptional.isEmpty()) {
            auditoriaService.registrarEvento(
                    MODULO_INTEGRACION_SWITCH,
                    ACCION_CONSULTAR_CUENTA_FAVORITA,
                    "CLIENTE",
                    cliente.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    CanalOrigenEnum.SWITCH,
                    "{\"rucEmpresa\":\"" + sanitizarJson(ruc) +
                            "\",\"existe\":true,\"valida\":false,\"motivo\":\"NO_CUENTA_FAVORITA\"}"
            );

            return IntegracionSwitchMapper.toCuentaFavoritaNoEncontradaResponse(ruc);
        }

        var cuentaFavorita = cuentaFavoritaOptional.get();
        boolean valida = cuentaFavorita.getEstado() == EstadoCuentaEnum.ACTIVA;
        boolean permiteDebito = valida
                && cuentaFavorita.getSaldoDisponible() != null
                && cuentaFavorita.getSaldoDisponible().compareTo(BigDecimal.ZERO) >= 0;

        String nombreBeneficiario = IntegracionSwitchMapper.resolverNombreBeneficiario(cuentaFavorita);

        auditoriaService.registrarEvento(
                MODULO_INTEGRACION_SWITCH,
                ACCION_CONSULTAR_CUENTA_FAVORITA,
                "CUENTA",
                cuentaFavorita.getId().toString(),
                valida ? ResultadoAuditoriaEnum.EXITOSO : ResultadoAuditoriaEnum.RECHAZADO,
                CanalOrigenEnum.SWITCH,
                "{\"rucEmpresa\":\"" + sanitizarJson(ruc) +
                        "\",\"numeroCuenta\":\"" + sanitizarJson(cuentaFavorita.getNumeroCuenta()) +
                        "\",\"estado\":\"" + cuentaFavorita.getEstado() +
                        "\",\"permiteDebito\":" + permiteDebito +
                        ",\"saldoDisponible\":" + cuentaFavorita.getSaldoDisponible() +
                        ",\"esFavoritaPagos\":" + cuentaFavorita.getEsFavoritaPagos() +
                        ",\"valida\":" + valida + "}"
        );

        return IntegracionSwitchMapper.toCuentaFavoritaResponse(
                ruc,
                cuentaFavorita.getNumeroCuenta(),
                cuentaFavorita.getEstado().name(),
                permiteDebito,
                cuentaFavorita.getSaldoDisponible(),
                cuentaFavorita.getEsFavoritaPagos(),
                valida,
                nombreBeneficiario
        );
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (request == null || esTextoVacio(request.usuario()) || esTextoVacio(request.contrasena())) {
            registrarLoginSwitch(
                    null,
                    "AUTENTICACION_SWITCH",
                    "N/A",
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "REQUEST_INVALIDO"
            );
            return loginFallido();
        }

        String usuario = request.usuario().trim();

        var credencialOptional = credencialWebRepository.findByUsuario(usuario);

        if (credencialOptional.isEmpty()) {
            registrarLoginSwitch(
                    usuario,
                    "CREDENCIAL_WEB",
                    usuario,
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CREDENCIAL_NO_EXISTE"
            );
            return loginFallido();
        }

        var credencial = credencialOptional.get();

        if (esTextoVacio(credencial.getPasswordHash())
                || !passwordEncoder.matches(request.contrasena(), credencial.getPasswordHash())) {
            registrarLoginSwitch(
                    usuario,
                    "CREDENCIAL_WEB",
                    credencial.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CREDENCIALES_INVALIDAS"
            );
            return loginFallido();
        }

        if (credencial.getEstado() != EstadoCredencialWebEnum.ACTIVO) {
            registrarLoginSwitch(
                    usuario,
                    "CREDENCIAL_WEB",
                    credencial.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CREDENCIAL_INACTIVA"
            );
            return loginFallido();
        }

        Cliente cliente = credencial.getCliente();

        if (cliente == null) {
            registrarLoginSwitch(
                    usuario,
                    "CREDENCIAL_WEB",
                    credencial.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CLIENTE_NO_ASOCIADO"
            );
            return loginFallido();
        }

        if (cliente.getTipoCliente() != TipoClienteEnum.JURIDICO) {
            registrarLoginSwitch(
                    usuario,
                    "CLIENTE",
                    cliente.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CLIENTE_NO_JURIDICO"
            );
            return loginFallido();
        }

        if (cliente.getEstado() != EstadoClienteEnum.ACTIVO) {
            registrarLoginSwitch(
                    usuario,
                    "CLIENTE",
                    cliente.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "CLIENTE_INACTIVO"
            );
            return loginFallido();
        }

        if (!Boolean.TRUE.equals(cliente.getActivoPagosMasivos())) {
            registrarLoginSwitch(
                    usuario,
                    "CLIENTE",
                    cliente.getId().toString(),
                    ResultadoAuditoriaEnum.RECHAZADO,
                    "EMPRESA_NO_HABILITADA_PAGOS_MASIVOS"
            );
            return loginFallido();
        }

        registrarLoginSwitch(
                usuario,
                "CLIENTE",
                cliente.getId().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                "LOGIN_EXITOSO"
        );

        return new LoginResponse(
                true,
                "EMPRESA",
                String.valueOf(credencial.getId()),
                String.valueOf(cliente.getId()),
                cliente.getIdentificacion(),
                credencial.getUsuario(),
                resolverNombreCliente(cliente),
                "EMPRESA",
                cliente.getEstado().name(),
                true
        );
    }

    @Override
    @Transactional
    public TransferenciaResponse ejecutarTransferencia(TransferenciaRequest request) {
        TransferenciaRequest requestSwitch = asegurarCanalSwitch(request);
        return transaccionService.ejecutarTransferencia(requestSwitch);
    }

    @Override
    @Transactional
    public LiquidacionServicioSwitchResponse liquidarServicio(LiquidacionServicioSwitchRequest request) {
        validarTotalLiquidacion(request);

        boolean permitirSobregiroLiquidacion = request.permiteSobregiro() == null
                || Boolean.TRUE.equals(request.permiteSobregiro());

        UUID uuidDebitoMatriz = transaccionService.debitarCuentaMatrizLiquidacion(
                request.cuentaMatriz(),
                request.totalDebitado(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna(),
                permitirSobregiroLiquidacion
        );

        UUID uuidCreditoIngresos = transaccionService.registrarMovimientoInstitucional(
                request.codigoCuentaIngresos(),
                "INGRESO_SERVICIO_MASIVO",
                TipoMovimientoEnum.CREDITO,
                request.subtotalComision(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        UUID uuidCreditoIva = transaccionService.registrarMovimientoInstitucional(
                request.codigoCuentaIva(),
                "IVA_SERVICIO_MASIVO",
                TipoMovimientoEnum.CREDITO,
                request.montoIva(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna()
        );

        auditoriaService.registrarEvento(
                MODULO_INTEGRACION_SWITCH,
                ACCION_LIQUIDAR_COMISION_IVA,
                "LIQUIDACION_SERVICIO",
                request.uuidGrupoOperacion().toString(),
                ResultadoAuditoriaEnum.EXITOSO,
                CanalOrigenEnum.SWITCH,
                "{\"cuentaMatriz\":\"" + sanitizarJson(request.cuentaMatriz()) +
                        "\",\"subtotalComision\":" + request.subtotalComision() +
                        ",\"montoIva\":" + request.montoIva() +
                        ",\"totalDebitado\":" + request.totalDebitado() +
                        ",\"permiteSobregiro\":" + permitirSobregiroLiquidacion +
                        ",\"codigoCuentaIngresos\":\"" + sanitizarJson(request.codigoCuentaIngresos()) +
                        "\",\"codigoCuentaIva\":\"" + sanitizarJson(request.codigoCuentaIva()) + "\"}"
        );

        return IntegracionSwitchMapper.toLiquidacionAplicadaResponse(
                uuidDebitoMatriz,
                uuidCreditoIngresos,
                uuidCreditoIva,
                request.uuidGrupoOperacion()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DiaHabilSwitchResponse consultarDiaHabil(LocalDate fecha) {
        boolean esFinSemana = fecha.getDayOfWeek() == DayOfWeek.SATURDAY
                || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;

        boolean esFeriado = feriadoRepository.existsById(fecha);
        boolean esDiaHabil = !esFinSemana && !esFeriado;
        LocalDate siguienteDiaHabil = feriadoService.calcularSiguienteDiaHabil(fecha);

        return IntegracionSwitchMapper.toDiaHabilResponse(
                fecha,
                esDiaHabil,
                esFinSemana,
                esFeriado,
                siguienteDiaHabil
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate siguienteDiaHabil(LocalDate fecha) {
        return feriadoService.calcularSiguienteDiaHabil(fecha);
    }

    private TransferenciaRequest asegurarCanalSwitch(TransferenciaRequest request) {
        return new TransferenciaRequest(
                request.cuentaOrigen(),
                request.cuentaDestino(),
                request.codigoSubtipo(),
                request.monto(),
                request.uuidOperacion(),
                request.uuidGrupoOperacion(),
                request.referenciaExterna(),
                request.descripcion(),
                CanalOrigenEnum.SWITCH,
                request.fechaNegocio(),
                request.usuarioCoreId(),
                request.credencialWebId()
        );
    }

    private void validarTotalLiquidacion(LiquidacionServicioSwitchRequest request) {
        BigDecimal totalCalculado = request.subtotalComision().add(request.montoIva());

        if (totalCalculado.compareTo(request.totalDebitado()) != 0) {
            throw new ValidationException("El total debitado debe ser igual a subtotalComision + montoIva");
        }
    }

    private LoginResponse loginFallido() {
        return new LoginResponse(
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private void registrarLoginSwitch(
            String usuario,
            String entidad,
            String entidadId,
            ResultadoAuditoriaEnum resultado,
            String motivo
    ) {
        auditoriaService.registrarEvento(
                MODULO_INTEGRACION_SWITCH,
                ACCION_LOGIN_SWITCH,
                entidad,
                entidadId,
                resultado,
                CanalOrigenEnum.SWITCH,
                "{\"usuario\":\"" + sanitizarJson(usuario) +
                        "\",\"motivo\":\"" + sanitizarJson(motivo) + "\"}"
        );
    }

    private String resolverNombreCliente(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        if (cliente.getTipoCliente() == TipoClienteEnum.JURIDICO) {
            return cliente.getRazonSocial();
        }

        String nombres = cliente.getNombres() == null ? "" : cliente.getNombres().trim();
        String apellidos = cliente.getApellidos() == null ? "" : cliente.getApellidos().trim();

        return (nombres + " " + apellidos).trim();
    }

    private boolean esTextoVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String sanitizarJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}