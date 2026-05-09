package com.banquito.core.branches.repository;

import com.banquito.core.branches.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
}
