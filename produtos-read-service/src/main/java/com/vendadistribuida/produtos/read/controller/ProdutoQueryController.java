package com.vendadistribuida.produtos.read.controller;

import com.vendadistribuida.produtos.read.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.read.service.ProdutoQueryService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoQueryController {

    private final ProdutoQueryService produtoQueryService;

    @GetMapping("/{id}")
    @Timed(value = "produtos.buscar-id", description = "Time taken to get product by id")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        log.info("Requisição para buscar produto por ID: {}", id);
        ProdutoResponse response = produtoQueryService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Timed(value = "produtos.listar", description = "Time taken to list products")
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        log.info("Requisição para listar produtos: page={}, size={}", page, size);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ProdutoResponse> response = produtoQueryService.listarPaginado(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/todos")
    @Timed(value = "produtos.listar-todos", description = "Time taken to list all products")
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        log.info("Requisição para listar todos os produtos");
        List<ProdutoResponse> response = produtoQueryService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoria}")
    @Timed(value = "produtos.buscar-categoria", description = "Time taken to get products by category")
    public ResponseEntity<Page<ProdutoResponse>> buscarPorCategoria(
            @PathVariable String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Requisição para buscar produtos por categoria: {}", categoria);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoResponse> response = produtoQueryService.buscarPorCategoria(categoria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar")
    @Timed(value = "produtos.buscar-termo", description = "Time taken to search products")
    public ResponseEntity<Page<ProdutoResponse>> buscar(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Requisição para buscar produtos com termo: {}", termo);
        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoResponse> response = produtoQueryService.buscar(termo, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    @Timed(value = "produtos.buscar-sku", description = "Time taken to get product by SKU")
    public ResponseEntity<ProdutoResponse> buscarPorSku(@PathVariable String sku) {
        log.info("Requisição para buscar produto por SKU: {}", sku);
        ProdutoResponse response = produtoQueryService.buscarPorSku(sku);
        return ResponseEntity.ok(response);
    }
}
