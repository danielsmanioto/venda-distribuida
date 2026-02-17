package com.vendadistribuida.vendas.service;

import com.vendadistribuida.vendas.domain.event.VendaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendaEventPublisher {

    private final KafkaTemplate<String, VendaEvent> kafkaTemplate;

    @Value("${kafka.topic.venda-criada}")
    private String topicVendaCriada;

    @Value("${kafka.topic.venda-processada}")
    private String topicVendaProcessada;

    @Value("${kafka.topic.venda-cancelada}")
    private String topicVendaCancelada;

    public void publishVendaCriada(VendaEvent event) {
        log.info("Publicando evento VENDA_CRIADA para venda ID: {}", event.getVendaId());
        kafkaTemplate.send(topicVendaCriada, event.getVendaId().toString(), event);
    }

    public void publishVendaProcessada(VendaEvent event) {
        log.info("Publicando evento VENDA_PROCESSADA para venda ID: {}", event.getVendaId());
        kafkaTemplate.send(topicVendaProcessada, event.getVendaId().toString(), event);
    }

    public void publishVendaCancelada(VendaEvent event) {
        log.info("Publicando evento VENDA_CANCELADA para venda ID: {}", event.getVendaId());
        kafkaTemplate.send(topicVendaCancelada, event.getVendaId().toString(), event);
    }
}
