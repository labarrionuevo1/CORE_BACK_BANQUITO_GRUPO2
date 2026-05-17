package com.banquito.core.transactions.controller;

import com.banquito.core.shared.response.ApiResponse;
import com.banquito.core.transactions.dto.api.SubtipoTransaccionResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.service.SubtipoTransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/core/subtipos-transaccion")
@RequiredArgsConstructor
public class SubtipoTransaccionController {

    private final SubtipoTransaccionService subtipoTransaccionService;

    @GetMapping
    public ApiResponse<List<SubtipoTransaccionResponse>> obtenerPorTipoMovimiento(
            @RequestParam("tipo") TipoMovimientoEnum tipo
    ) {
        List<SubtipoTransaccionResponse> subtipos = subtipoTransaccionService.obtenerPorTipoMovimiento(tipo);
        return ApiResponse.ok("Subtipos de transacción obtenidos", subtipos);
    }
}
