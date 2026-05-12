package com.banquito.core.customers.service;

import com.banquito.core.customers.dto.api.ClienteRequest;
import com.banquito.core.customers.dto.api.ClienteResponse;
import com.banquito.core.customers.enums.EstadoClienteEnum;
import com.banquito.core.customers.model.Cliente;

import java.util.List;

public interface ClienteService {
    
    List<ClienteResponse> listar();
    
    Cliente obtenerEntidad(Integer id);
    
    ClienteResponse obtener(Integer id);
    
    ClienteResponse obtenerPorIdentificacion(String identificacion);

    ClienteResponse crear(ClienteRequest request);
    
    ClienteResponse cambiarEstado(Integer id, EstadoClienteEnum nuevoEstado);
    
    boolean validarEmpresaParaPagosMasivos(String ruc);
    
}
