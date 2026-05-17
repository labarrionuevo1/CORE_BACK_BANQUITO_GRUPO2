package com.banquito.core.transactions.controller;

import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.api.SubtipoTransaccionResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.SubtipoTransaccionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/core/subtipos-transaccion")
public class SubtipoTransaccionController {

    private final SubtipoTransaccionService service;

    public SubtipoTransaccionController(SubtipoTransaccionService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_CORE', 'CAJERO', 'SUPERVISOR_AGENCIA', 'AUDITOR', 'SISTEMA')")
    public ApiResponse<List<SubtipoTransaccionResponse>> obtenerPorTipo(
            @RequestParam(required = false) String tipo
    ) {
        if (tipo != null) {
            TipoMovimientoEnum tipoEnum = TipoMovimientoEnum.valueOf(tipo.toUpperCase());
            return ApiResponse.ok("Subtipos obtenidos", service.obtenerPorTipoMovimiento(tipoEnum));
        }
        return ApiResponse.ok("Todos los subtipos obtenidos", service.obtenerTodos());
    }
}
