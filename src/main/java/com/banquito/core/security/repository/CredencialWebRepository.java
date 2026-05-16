package com.banquito.core.security.repository;

import com.banquito.core.security.enums.EstadoCredencialWebEnum;
import com.banquito.core.security.model.CredencialWeb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialWebRepository extends JpaRepository<CredencialWeb, Integer> {

    Optional<CredencialWeb> findByUsuario(String usuario);

    boolean existsByClienteIdAndEstado(Integer clienteId, EstadoCredencialWebEnum estado);

    Optional<CredencialWeb> findByUsuarioAndClienteId(String usuario, Integer clienteId);
}
