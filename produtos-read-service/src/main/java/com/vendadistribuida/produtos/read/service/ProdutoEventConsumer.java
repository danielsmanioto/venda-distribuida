package com.vendadistribuida.produtos.read.service;

import com.vendadistribuida.produtos.read.domain.event.ProdutoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoEventConsumer {

    private final CacheManager cacheManager;

    @RabbitListener(queues = "${rabbitmq.queue.produto-created}")
    public void handleProdutoCreated(ProdutoEvent event) {
        log.info("Evento CREATED recebido para produto ID: {}", event.getId());
        invalidarCache(event.getId());
    }

    @RabbitListener(queues = "${rabbitmq.queue.produto-updated}")
    public void handleProdutoUpdated(ProdutoEvent event) {
        log.info("Evento UPDATED recebido para produto ID: {}", event.getId());
        invalidarCache(event.getId());
    }

    @RabbitListener(queues = "${rabbitmq.queue.produto-deleted}")
    public void handleProdutoDeleted(ProdutoEvent event) {
        log.info("Evento DELETED recebido para produto ID: {}", event.getId());
        invalidarCache(event.getId());
    }

    private void invalidarCache(Long produtoId) {
        try {
            // Invalidar cache específico do produto
            if (cacheManager.getCache("produto") != null) {
                cacheManager.getCache("produto").evict(produtoId);
                log.info("Cache invalidado para produto ID: {}", produtoId);
            }

            // Invalidar cache de listagens
            if (cacheManager.getCache("produtos-all") != null) {
                cacheManager.getCache("produtos-all").clear();
                log.info("Cache 'produtos-all' limpo");
            }

            if (cacheManager.getCache("produtos-categoria") != null) {
                cacheManager.getCache("produtos-categoria").clear();
                log.info("Cache 'produtos-categoria' limpo");
            }

            if (cacheManager.getCache("produto-sku") != null) {
                cacheManager.getCache("produto-sku").clear();
                log.info("Cache 'produto-sku' limpo");
            }
        } catch (Exception e) {
            log.error("Erro ao invalidar cache: {}", e.getMessage(), e);
        }
    }
}
