package com.vendadistribuida.vendas.controller;

import com.vendadistribuida.vendas.domain.dto.VendaRequest;
import com.vendadistribuida.vendas.domain.dto.VendaResponse;
import com.vendadistribuida.vendas.service.VendaService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    @Timed(value = "vendas.criar", description = "Time taken to create sale")
    public ResponseEntity<VendaResponse> criar(@Valid @RequestBody VendaRequest request) {
        log.info("Requisição para criar venda");
        VendaResponse response = vendaService.criarVenda(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Timed(value = "vendas.buscar", description = "Time taken to get sale by id")
    public ResponseEntity<VendaResponse> buscarPorId(@PathVariable Long id) {
        log.info("Requisição para buscar venda: {}", id);
        VendaResponse response = vendaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Timed(value = "vendas.buscar-usuario", description = "Time taken to get sales by user")
    public ResponseEntity<List<VendaResponse>> buscarPorUsuario(@PathVariable Long usuarioId) {
        log.info("Requisição para buscar vendas do usuário: {}", usuarioId);
        List<VendaResponse> response = vendaService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Timed(value = "vendas.listar", description = "Time taken to list sales")
    public ResponseEntity<Page<VendaResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("Requisição para listar vendas: page={}, size={}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<VendaResponse> response = vendaService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    @Timed(value = "vendas.cancelar", description = "Time taken to cancel sale")
    public ResponseEntity<VendaResponse> cancelar(@PathVariable Long id) {
        log.info("Requisição para cancelar venda: {}", id);
        VendaResponse response = vendaService.cancelarVenda(id);
        return ResponseEntity.ok(response);
    }
}
