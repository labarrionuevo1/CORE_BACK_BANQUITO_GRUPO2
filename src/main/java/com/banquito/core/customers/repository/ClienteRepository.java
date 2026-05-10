package com.banquito.core.customers.repository;

import com.banquito.core.customers.model.Cliente;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.enums.TipoClienteEnum;
import com.banquito.core.customers.enums.TipoIdentificacionEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByTipoIdentificacionAndIdentificacion(TipoIdentificacionEnum tipoIdentificacion, String identificacion);
    Optional<Cliente> findByIdentificacion(String identificacion);
    List<Cliente> findByTipoClienteAndActivoPagosMasivosAndEstado(TipoClienteEnum tipoCliente, Boolean activoPagosMasivos, EstadoClienteEnum estado);
}
