package com.banquito.core.transactions.repository;

import com.banquito.core.accounts.model.Cuenta;
import com.banquito.core.transactions.model.TransaccionCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransaccionCuentaRepository extends JpaRepository<TransaccionCuenta, Long> {
    
     @Query("SELECT COUNT(t) > 0 FROM TransaccionCuenta t WHERE t.cuenta = :cuenta AND t.uuidTransaccion = :uuid AND t.fechaNegocio = :fechaNegocio")
    boolean existsByCuentaAndUuidTransaccionAndFechaNegocio(
            @Param("cuenta") Cuenta cuenta,
            @Param("uuid") UUID uuid,
            @Param("fechaNegocio") LocalDate fechaNegocio
    );

    @Query("SELECT t FROM TransaccionCuenta t WHERE t.cuenta.numeroCuenta = :numeroCuenta ORDER BY t.fechaTransaccion DESC")
    List<TransaccionCuenta> findUltimosMovimientosPorNumeroCuenta(
            @Param("numeroCuenta") String numeroCuenta
    );

    Optional<TransaccionCuenta> findByUuidTransaccion(UUID uuidTransaccion);
}
