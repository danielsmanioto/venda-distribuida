package com.vendadistribuida.produtos.write.service;

import com.vendadistribuida.produtos.write.domain.dto.ProdutoRequest;
import com.vendadistribuida.produtos.write.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.write.domain.entity.Produto;
import com.vendadistribuida.produtos.write.domain.event.ProdutoEvent;
import com.vendadistribuida.produtos.write.repository.ProdutoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoEventPublisher eventPublisher;

    @Transactional
    @CircuitBreaker(name = "produtos-write", fallbackMethod = "criarFallback")
    @RateLimiter(name = "produtos-write")
    public ProdutoResponse criar(ProdutoRequest request) {
        log.info("Criando novo produto: {}", request.getNome());

        if (request.getSku() != null && produtoRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU já cadastrado: " + request.getSku());
        }

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .estoque(request.getEstoque())
                .categoria(request.getCategoria())
                .sku(request.getSku())
                .imagemUrl(request.getImagemUrl())
                .ativo(request.getAtivo() != null ? request.getAtivo() : true)
                .build();

        Produto salvo = produtoRepository.save(produto);
        log.info("Produto criado com sucesso: ID {}", salvo.getId());

        // Publicar evento
        ProdutoEvent event = buildEvent(salvo, ProdutoEvent.EventType.CREATED);
        eventPublisher.publishCreated(event);

        return mapToResponse(salvo);
    }

    @Transactional
    @CircuitBreaker(name = "produtos-write", fallbackMethod = "atualizarFallback")
    @RateLimiter(name = "produtos-write")
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        log.info("Atualizando produto: {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        if (request.getSku() != null && !request.getSku().equals(produto.getSku())
                && produtoRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU já cadastrado: " + request.getSku());
        }

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        produto.setCategoria(request.getCategoria());
        produto.setSku(request.getSku());
        produto.setImagemUrl(request.getImagemUrl());
        if (request.getAtivo() != null) {
            produto.setAtivo(request.getAtivo());
        }

        Produto atualizado = produtoRepository.save(produto);
        log.info("Produto atualizado com sucesso: ID {}", id);

        // Publicar evento
        ProdutoEvent event = buildEvent(atualizado, ProdutoEvent.EventType.UPDATED);
        eventPublisher.publishUpdated(event);

        return mapToResponse(atualizado);
    }

    @Transactional
    @CircuitBreaker(name = "produtos-write", fallbackMethod = "deletarFallback")
    @RateLimiter(name = "produtos-write")
    public void deletar(Long id) {
        log.info("Deletando produto: {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        produto.setAtivo(false);
        produtoRepository.save(produto);

        log.info("Produto deletado (soft delete) com sucesso: ID {}", id);

        // Publicar evento
        ProdutoEvent event = buildEvent(produto, ProdutoEvent.EventType.DELETED);
        eventPublisher.publishDeleted(event);
    }

    @Transactional
    @CircuitBreaker(name = "produtos-write")
    @RateLimiter(name = "produtos-write")
    public ProdutoResponse atualizarEstoque(Long id, Integer quantidade) {
        log.info("Atualizando estoque do produto {}: {}", id, quantidade);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        produto.setEstoque(quantidade);
        Produto atualizado = produtoRepository.save(produto);

        log.info("Estoque atualizado com sucesso: ID {}", id);

        // Publicar evento
        ProdutoEvent event = buildEvent(atualizado, ProdutoEvent.EventType.UPDATED);
        eventPublisher.publishUpdated(event);

        return mapToResponse(atualizado);
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

    private ProdutoEvent buildEvent(Produto produto, ProdutoEvent.EventType eventType) {
        return ProdutoEvent.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .estoque(produto.getEstoque())
                .categoria(produto.getCategoria())
                .sku(produto.getSku())
                .imagemUrl(produto.getImagemUrl())
                .ativo(produto.getAtivo())
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }

    // Fallback methods
    private ProdutoResponse criarFallback(ProdutoRequest request, Exception ex) {
        log.error("Fallback criar acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }

    private ProdutoResponse atualizarFallback(Long id, ProdutoRequest request, Exception ex) {
        log.error("Fallback atualizar acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }

    private void deletarFallback(Long id, Exception ex) {
        log.error("Fallback deletar acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }
}
