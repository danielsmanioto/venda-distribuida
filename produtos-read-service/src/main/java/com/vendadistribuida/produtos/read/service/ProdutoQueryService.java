package com.vendadistribuida.produtos.read.service;

import com.vendadistribuida.produtos.read.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.read.domain.entity.Produto;
import com.vendadistribuida.produtos.read.repository.ProdutoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoQueryService {

    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "produto", key = "#id")
    @CircuitBreaker(name = "produtos-read", fallbackMethod = "buscarPorIdFallback")
    @RateLimiter(name = "produtos-read")
    public ProdutoResponse buscarPorId(Long id) {
        log.info("Buscando produto por ID: {}", id);
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
        return mapToResponse(produto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "produtos-all")
    @CircuitBreaker(name = "produtos-read")
    @RateLimiter(name = "produtos-read")
    public List<ProdutoResponse> listarTodos() {
        log.info("Listando todos os produtos");
        return produtoRepository.findByAtivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "produtos-read")
    @RateLimiter(name = "produtos-read")
    public Page<ProdutoResponse> listarPaginado(Pageable pageable) {
        log.info("Listando produtos com paginação: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        return produtoRepository.findByAtivoTrue(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "produtos-categoria", key = "#categoria")
    @CircuitBreaker(name = "produtos-read")
    @RateLimiter(name = "produtos-read")
    public Page<ProdutoResponse> buscarPorCategoria(String categoria, Pageable pageable) {
        log.info("Buscando produtos por categoria: {}", categoria);
        return produtoRepository.findByCategoriaAndAtivoTrue(categoria, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "produtos-read")
    @RateLimiter(name = "produtos-read")
    public Page<ProdutoResponse> buscar(String termo, Pageable pageable) {
        log.info("Buscando produtos por termo: {}", termo);
        return produtoRepository.buscarPorTermo(termo, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "produto-sku", key = "#sku")
    @CircuitBreaker(name = "produtos-read")
    @RateLimiter(name = "produtos-read")
    public ProdutoResponse buscarPorSku(String sku) {
        log.info("Buscando produto por SKU: {}", sku);
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com SKU: " + sku));
        return mapToResponse(produto);
    }

    private ProdutoResponse mapToResponse(Produto produto) {
        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .estoque(produto.getEstoque())
                .categoria(produto.getCategoria())
                .sku(produto.getSku())
                .imagemUrl(produto.getImagemUrl())
                .ativo(produto.getAtivo())
                .criadoEm(produto.getCriadoEm())
                .atualizadoEm(produto.getAtualizadoEm())
                .build();
    }

    // Fallback method
    private ProdutoResponse buscarPorIdFallback(Long id, Exception ex) {
        log.error("Fallback buscarPorId acionado para ID {}: {}", id, ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }
}
