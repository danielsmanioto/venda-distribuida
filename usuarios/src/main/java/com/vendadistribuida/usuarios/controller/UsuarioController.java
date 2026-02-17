package com.vendadistribuida.usuarios.controller;

import com.vendadistribuida.usuarios.domain.dto.RegistroRequest;
import com.vendadistribuida.usuarios.domain.dto.UsuarioResponse;
import com.vendadistribuida.usuarios.service.UsuarioService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

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
        List<UsuarioResponse> response = usuarioService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Timed(value = "usuarios.atualizar", description = "Time taken to update user")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegistroRequest request) {
        log.info("Atualizando usuário: {}", id);
        UsuarioResponse response = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(response);
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
