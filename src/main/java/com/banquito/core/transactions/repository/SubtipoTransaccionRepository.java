package com.banquito.core.transactions.repository;

import com.banquito.core.shared.enums.EstadoCatalogoEnum;
import com.banquito.core.transactions.enums.TipoMovimientoEnum;
import com.banquito.core.transactions.model.SubtipoTransaccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtipoTransaccionRepository extends JpaRepository<SubtipoTransaccion, Integer> {

    Optional<SubtipoTransaccion> findByCodigo(String codigo);

    List<SubtipoTransaccion> findByTipoMovimientoBaseAndEstado(TipoMovimientoEnum tipoMovimientoBase, EstadoCatalogoEnum estado);
}
