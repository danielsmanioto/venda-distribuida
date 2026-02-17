package com.vendadistribuida.vendas.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.venda-criada}")
    private String topicVendaCriada;

    @Value("${kafka.topic.venda-processada}")
    private String topicVendaProcessada;

    @Value("${kafka.topic.venda-cancelada}")
    private String topicVendaCancelada;

    @Bean
    public NewTopic vendaCriadaTopic() {
        return TopicBuilder.name(topicVendaCriada)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic vendaProcessadaTopic() {
        return TopicBuilder.name(topicVendaProcessada)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic vendaCanceladaTopic() {
        return TopicBuilder.name(topicVendaCancelada)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
