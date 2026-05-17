package com.banquito.core.notifications.service;

import com.banquito.core.notifications.dto.internal.EnviarCorreoRequest;
import com.banquito.core.shared.enums.CanalOrigenEnum;

public interface NotificacionService {

    void enviarCorreo(
            EnviarCorreoRequest request,
            String accionOrigen,
            String entidadOrigen,
            String entidadIdOrigen,
            CanalOrigenEnum canalOrigen
    );
}
