package com.vendadistribuida.usuarios.controller;

import com.vendadistribuida.usuarios.domain.dto.AuthResponse;
import com.vendadistribuida.usuarios.domain.dto.LoginRequest;
import com.vendadistribuida.usuarios.domain.dto.RegistroRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.service.UsuarioService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Timed(value = "auth.registro", description = "Time taken to register user")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        log.info("Requisição de registro recebida: {}", request.getEmail());
        UsuarioResponse response = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Timed(value = "auth.login", description = "Time taken to login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requisição de login recebida: {}", request.getEmail());
        AuthResponse response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }
}
