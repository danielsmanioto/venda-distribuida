package com.vendadistribuida.produtos.write.service;

import com.vendadistribuida.produtos.write.domain.event.ProdutoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.produto}")
    private String exchange;

    @Value("${rabbitmq.routing-key.created}")
    private String routingKeyCreated;

    @Value("${rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;

    @Value("${rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;

    public void publishCreated(ProdutoEvent event) {
        log.info("Publicando evento CREATED para produto ID: {}", event.getId());
        rabbitTemplate.convertAndSend(exchange, routingKeyCreated, event);
    }

    public void publishUpdated(ProdutoEvent event) {
        log.info("Publicando evento UPDATED para produto ID: {}", event.getId());
        rabbitTemplate.convertAndSend(exchange, routingKeyUpdated, event);
    }

    public void publishDeleted(ProdutoEvent event) {
        log.info("Publicando evento DELETED para produto ID: {}", event.getId());
        rabbitTemplate.convertAndSend(exchange, routingKeyDeleted, event);
    }
}
