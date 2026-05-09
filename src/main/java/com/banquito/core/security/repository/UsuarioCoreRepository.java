package com.banquito.core.security.repository;

import com.banquito.core.security.model.UsuarioCore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioCoreRepository extends JpaRepository<UsuarioCore, Integer> {

    Optional<UsuarioCore> findByUsuario(String usuario);
}
