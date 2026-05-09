package com.banquito.core.security.dto.response;

import com.banquito.core.security.enums.EstadoCredencialWebEnum;

public record CredencialWebResponse(Integer id, Integer clienteId, String usuario, EstadoCredencialWebEnum estado) {}
