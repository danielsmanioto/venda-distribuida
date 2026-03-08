package com.vendadistribuida.usuarios.controller;

import com.vendadistribuida.usuarios.domain.dto.RegistroRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.service.UsuarioService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Timed(value = "usuarios.buscar", description = "Time taken to get user by id")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando usuário: {}", id);
        UsuarioResponse response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Timed(value = "usuarios.listar", description = "Time taken to list all users")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        log.info("Listando todos os usuários");
        List<UsuarioResponse> response = usuarioService.listar();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Timed(value = "usuarios.atualizar", description = "Time taken to update user")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        log.info("Atualizando usuário: {}", id);
        UsuarioResponse response = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Timed(value = "usuarios.criar", description = "Time taken to create user")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        log.info("Criando usuário: {}", request.getEmail());
        UsuarioResponse response = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed(value = "usuarios.deletar", description = "Time taken to delete user")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Deletando usuário: {}", id);
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
