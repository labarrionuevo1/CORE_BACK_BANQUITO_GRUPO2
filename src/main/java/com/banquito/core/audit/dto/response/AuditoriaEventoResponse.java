package com.banquito.core.audit.dto.response;

import com.banquito.core.audit.enums.ResultadoAuditoriaEnum;
import com.banquito.core.shared.enums.CanalOrigenEnum;
import java.time.LocalDateTime;

public record AuditoriaEventoResponse(Long id, String modulo, String accion, String entidad, String entidadId, ResultadoAuditoriaEnum resultado, CanalOrigenEnum canalOrigen, LocalDateTime fechaEvento) {}
