package com.banquito.core.branches.repository;

import com.banquito.core.branches.model.Sucursal;
import com.banquito.core.branches.enums.EstadoSucursalEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
    
    List<Sucursal> findByEstado(EstadoSucursalEnum estado);
    
    Optional<Sucursal> findByCodigoSucursal(String codigoSucursal);
}
