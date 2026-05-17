package com.banquito.core.transactions.service;

import com.banquito.core.transactions.dto.api.SubtipoTransaccionResponse;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import java.util.List;

public interface SubtipoTransaccionService {
    List<SubtipoTransaccionResponse> obtenerPorTipoMovimiento(TipoMovimientoEnum tipoMovimiento);
}
