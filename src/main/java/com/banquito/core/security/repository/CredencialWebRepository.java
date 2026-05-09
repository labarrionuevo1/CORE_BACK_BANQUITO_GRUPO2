package com.banquito.core.security.repository;

import com.banquito.core.security.model.CredencialWeb;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredencialWebRepository extends JpaRepository<CredencialWeb, Integer> {

    Optional<CredencialWeb> findByUsuario(String usuario);
}
