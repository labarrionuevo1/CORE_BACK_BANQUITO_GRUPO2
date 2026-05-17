package com.banquito.core.integration.switchapi.mapper;

import com.banquito.core.accounts.enums.EstadoCuentaEnum;
import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.customers.model.Cliente;
import com.banquito.core.integration.switchapi.dto.api.DiaHabilSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.LiquidacionServicioSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCredencialEmpresaSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaDestinoSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarCuentaMatrizSwitchResponse;
import com.banquito.core.integration.switchapi.dto.api.ValidarEmpresaSwitchResponse;
import com.banquito.core.security.model.CredencialWeb;

import java.time.LocalDate;
import java.util.UUID;

public final class IntegracionSwitchMapper {

    private IntegracionSwitchMapper() {
    }

    public static ValidarEmpresaSwitchResponse toEmpresaNoExisteResponse(String ruc) {
        return new ValidarEmpresaSwitchResponse(
                ruc,
                false,
                null,
                null,
                false,
                false,
                false,
                "EMPRESA_NO_EXISTE",
                "La empresa emisora no existe."
        );
    }

    public static ValidarEmpresaSwitchResponse toEmpresaResponse(
            String ruc,
            Cliente cliente,
            Boolean credencialWebValida,
            Boolean habilitada
    ) {
        return new ValidarEmpresaSwitchResponse(
                ruc,
                true,
                cliente.getTipoCliente(),
                cliente.getEstado(),
                Boolean.TRUE.equals(cliente.getActivoPagosMasivos()),
                Boolean.TRUE.equals(credencialWebValida),
                Boolean.TRUE.equals(habilitada),
                Boolean.TRUE.equals(habilitada) ? "EMPRESA_HABILITADA" : "EMPRESA_NO_HABILITADA",
                Boolean.TRUE.equals(habilitada)
                        ? "Empresa habilitada para pagos masivos."
                        : "Empresa no habilitada para pagos masivos."
        );
    }

    public static CuentaFavoritaPagosResponse toCuentaFavoritaResponse(
            String rucEmpresa,
            String numeroCuenta,
            String estado,
            Boolean permiteDebito,
            java.math.BigDecimal saldoDisponible,
            Boolean esFavoritaPagos,
            Boolean valida
    ) {
        String codigo = valida ? "CUENTA_FAVORITA_VALIDA" : "CUENTA_FAVORITA_INVALIDA";
        String mensaje = valida ? "Cuenta favorita valida para pagos masivos." : "Cuenta favorita no valida para pagos masivos.";
        return new CuentaFavoritaPagosResponse(
                rucEmpresa,
                true,
                numeroCuenta,
                estado,
                permiteDebito,
                saldoDisponible,
                esFavoritaPagos,
                valida,
                codigo,
                mensaje
        );
    }

    public static CuentaFavoritaPagosResponse toCuentaFavoritaNoExisteResponse(String rucEmpresa) {
        return new CuentaFavoritaPagosResponse(
                rucEmpresa,
                false,
                null,
                null,
                false,
                null,
                false,
                false,
                "CUENTA_FAVORITA_EMPRESA_NO_EXISTE",
                "Empresa no existe o no tiene cuenta favorita para pagos masivos."
        );
    }

    public static CuentaFavoritaPagosResponse toCuentaFavoritaNoEncontradaResponse(String rucEmpresa) {
        return new CuentaFavoritaPagosResponse(
                rucEmpresa,
                true,
                null,
                null,
                false,
                null,
                false,
                false,
                "CUENTA_FAVORITA_NO_ENCONTRADA",
                "No se encontró cuenta favorita para pagos masivos."
        );
    }

    public static ValidarCuentaMatrizSwitchResponse toCuentaMatrizEmpresaNoExisteResponse(
            String ruc,
            String numeroCuenta,
            Cuenta cuenta
    ) {
        return new ValidarCuentaMatrizSwitchResponse(
                numeroCuenta,
                ruc,
                cuenta != null,
                false,
                cuenta != null ? cuenta.getEstado() : null,
                false,
                cuenta != null ? cuenta.getSaldoContable() : null,
                cuenta != null ? cuenta.getSaldoDisponible() : null,
                cuenta != null && Boolean.TRUE.equals(cuenta.getPermiteSobregiro()),
                cuenta != null ? cuenta.getLimiteSobregiro() : null,
                false,
                "EMPRESA_NO_EXISTE",
                "La empresa emisora no existe."
        );
    }

    public static ValidarCuentaMatrizSwitchResponse toCuentaMatrizNoExisteResponse(String ruc, String numeroCuenta) {
        return new ValidarCuentaMatrizSwitchResponse(
                numeroCuenta,
                ruc,
                false,
                false,
                null,
                false,
                null,
                null,
                false,
                null,
                false,
                "CUENTA_MATRIZ_NO_EXISTE",
                "La cuenta matriz no existe."
        );
    }

    public static ValidarCuentaMatrizSwitchResponse toCuentaMatrizResponse(
            String ruc,
            Cuenta cuenta,
            Boolean perteneceEmpresa,
            Boolean permiteDebito,
            Boolean valida
    ) {
        return new ValidarCuentaMatrizSwitchResponse(
                cuenta.getNumeroCuenta(),
                ruc,
                true,
                Boolean.TRUE.equals(perteneceEmpresa),
                cuenta.getEstado(),
                Boolean.TRUE.equals(permiteDebito),
                cuenta.getSaldoContable(),
                cuenta.getSaldoDisponible(),
                Boolean.TRUE.equals(cuenta.getPermiteSobregiro()),
                cuenta.getLimiteSobregiro(),
                Boolean.TRUE.equals(valida),
                Boolean.TRUE.equals(valida) ? "CUENTA_MATRIZ_VALIDA" : "CUENTA_MATRIZ_INVALIDA",
                Boolean.TRUE.equals(valida)
                        ? "Cuenta matriz valida para pagos masivos."
                        : "Cuenta matriz no valida para pagos masivos."
        );
    }

    public static ValidarCuentaDestinoSwitchResponse toCuentaDestinoNoExisteResponse(
            String numeroCuenta,
            String identificacionBeneficiario
    ) {
        return new ValidarCuentaDestinoSwitchResponse(
                numeroCuenta,
                identificacionBeneficiario,
                false,
                false,
                null,
                false,
                false,
                false,
                "CUENTA_DESTINO_NO_EXISTE",
                "La cuenta destino no existe."
        );
    }

    public static ValidarCuentaDestinoSwitchResponse toCuentaDestinoResponse(
            String identificacionBeneficiario,
            Cuenta cuenta,
            Boolean perteneceBeneficiario,
            Boolean permiteDeposito,
            Boolean bloqueada,
            Boolean valida
    ) {
        return new ValidarCuentaDestinoSwitchResponse(
                cuenta.getNumeroCuenta(),
                identificacionBeneficiario,
                true,
                Boolean.TRUE.equals(perteneceBeneficiario),
                cuenta.getEstado(),
                Boolean.TRUE.equals(permiteDeposito),
                Boolean.TRUE.equals(bloqueada),
                Boolean.TRUE.equals(valida),
                Boolean.TRUE.equals(valida) ? "CUENTA_DESTINO_VALIDA" : "CUENTA_DESTINO_INVALIDA",
                Boolean.TRUE.equals(valida)
                        ? "Cuenta destino valida para recibir pagos."
                        : "Cuenta destino no valida para recibir pagos."
        );
    }

    public static ValidarCredencialEmpresaSwitchResponse toCredencialEmpresaNoExisteResponse(
            String ruc,
            String username
    ) {
        return new ValidarCredencialEmpresaSwitchResponse(
                ruc,
                username,
                false,
                false,
                null,
                false,
                "EMPRESA_NO_EXISTE",
                "La empresa emisora no existe."
        );
    }

    public static ValidarCredencialEmpresaSwitchResponse toCredencialNoExisteResponse(
            String ruc,
            String username
    ) {
        return new ValidarCredencialEmpresaSwitchResponse(
                ruc,
                username,
                false,
                false,
                null,
                false,
                "CREDENCIAL_NO_EXISTE",
                "La credencial web no existe."
        );
    }

    public static ValidarCredencialEmpresaSwitchResponse toCredencialEmpresaResponse(
            String ruc,
            String username,
            CredencialWeb credencial,
            Boolean perteneceEmpresa,
            Boolean valida
    ) {
        return new ValidarCredencialEmpresaSwitchResponse(
                ruc,
                username,
                true,
                Boolean.TRUE.equals(perteneceEmpresa),
                credencial.getEstado(),
                Boolean.TRUE.equals(valida),
                Boolean.TRUE.equals(valida) ? "CREDENCIAL_EMPRESARIAL_VALIDA" : "CREDENCIAL_EMPRESARIAL_INVALIDA",
                Boolean.TRUE.equals(valida)
                        ? "Credencial empresarial valida para pagos masivos."
                        : "Credencial empresarial no valida para pagos masivos."
        );
    }

    public static DiaHabilSwitchResponse toDiaHabilResponse(
            LocalDate fecha,
            Boolean esDiaHabil,
            Boolean esFinSemana,
            Boolean esFeriado,
            LocalDate siguienteDiaHabil
    ) {
        return new DiaHabilSwitchResponse(
                fecha,
                Boolean.TRUE.equals(esDiaHabil),
                Boolean.TRUE.equals(esFinSemana),
                Boolean.TRUE.equals(esFeriado),
                siguienteDiaHabil,
                Boolean.TRUE.equals(esDiaHabil) ? "DIA_HABIL" : "DIA_NO_HABIL",
                Boolean.TRUE.equals(esDiaHabil)
                        ? "La fecha indicada es dia habil."
                        : "La fecha indicada no es dia habil."
        );
    }

    public static LiquidacionServicioSwitchResponse toLiquidacionAplicadaResponse(
            UUID uuidDebitoMatriz,
            UUID uuidCreditoIngresos,
            UUID uuidCreditoIva,
            UUID uuidGrupoOperacion
    ) {
        return new LiquidacionServicioSwitchResponse(
                "APLICADA",
                uuidDebitoMatriz,
                uuidCreditoIngresos,
                uuidCreditoIva,
                uuidGrupoOperacion
        );
    }

    public static boolean estadoPermiteDeposito(EstadoCuentaEnum estadoCuenta) {
        return estadoCuenta == EstadoCuentaEnum.ACTIVA;
    }
}