package com.banquito.core.customers.controller;

import com.banquito.core.customers.dto.request.ClienteRequest;
import com.banquito.core.customers.service.ClienteService;
import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService service;
    @GetMapping public ApiResponse<?> listar() { return ApiResponse.ok("Clientes obtenidos", service.listar()); }
    @GetMapping("/{id}") public ApiResponse<?> obtener(@PathVariable Integer id) { return ApiResponse.ok("Cliente obtenido", service.obtener(id)); }
    @PostMapping public ApiResponse<?> crear(@Valid @RequestBody ClienteRequest request) { return ApiResponse.ok("Cliente creado", service.crear(request)); }
}
