package com.banquito.core.institutional.dto.api;

import com.banquito.core.institutional.enums.EstadoCuentaInstitucionalEnum;
import com.banquito.core.institutional.enums.TipoCuentaInstitucionalEnum;
import java.math.BigDecimal;

public record CuentaInstitucionalResponse(Integer id, String numeroCuenta, String codigo, String nombre, TipoCuentaInstitucionalEnum tipoCuenta, BigDecimal saldoContable, EstadoCuentaInstitucionalEnum estado) {}
