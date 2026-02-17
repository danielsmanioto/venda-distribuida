package com.vendadistribuida.vendas.service;

import com.vendadistribuida.vendas.domain.dto.ProdutoDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final WebClient produtosWebClient;

    @CircuitBreaker(name = "produtos", fallbackMethod = "buscarProdutoFallback")
    public ProdutoDTO buscarProduto(Long produtoId) {
        log.info("Buscando produto ID: {}", produtoId);
        
        return produtosWebClient.get()
                .uri("/api/produtos/{id}", produtoId)
                .retrieve()
                .bodyToMono(ProdutoDTO.class)
                .timeout(Duration.ofSeconds(5))
                .block();
    }

    private ProdutoDTO buscarProdutoFallback(Long produtoId, Exception ex) {
        log.error("Fallback buscarProduto acionado para produto ID {}: {}", produtoId, ex.getMessage());
        throw new RuntimeException("Serviço de produtos indisponível no momento");
    }
}
