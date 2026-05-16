package com.banquito.core.security.dto.api;

public record LoginPagosMasivosResponse(
    Boolean autenticado,
    String tipoUsuario,
    Integer credencialWebId,
    Integer clienteId,
    String rucEmpresa,
    String usuario,
    String nombre,
    String estado
) {}
