package com.vendadistribuida.produtos.write.controller;

import com.vendadistribuida.produtos.write.domain.dto.ProdutoRequest;
import com.vendadistribuida.produtos.write.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.write.service.ProdutoService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    @Timed(value = "produtos.criar", description = "Time taken to create product")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        log.info("Requisição para criar produto: {}", request.getNome());
        ProdutoResponse response = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Timed(value = "produtos.atualizar", description = "Time taken to update product")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request) {
        log.info("Requisição para atualizar produto: {}", id);
        ProdutoResponse response = produtoService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Timed(value = "produtos.deletar", description = "Time taken to delete product")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Requisição para deletar produto: {}", id);
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque")
    @Timed(value = "produtos.atualizar-estoque", description = "Time taken to update stock")
    public ResponseEntity<ProdutoResponse> atualizarEstoque(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        log.info("Requisição para atualizar estoque do produto {}: {}", id, quantidade);
        ProdutoResponse response = produtoService.atualizarEstoque(id, quantidade);
        return ResponseEntity.ok(response);
    }
}
