package com.vendadistribuida.produtos.write.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.produto}")
    private String exchange;

    @Value("${rabbitmq.queue.produto-created}")
    private String queueCreated;

    @Value("${rabbitmq.queue.produto-updated}")
    private String queueUpdated;

    @Value("${rabbitmq.queue.produto-deleted}")
    private String queueDeleted;

    @Value("${rabbitmq.routing-key.created}")
    private String routingKeyCreated;

    @Value("${rabbitmq.routing-key.updated}")
    private String routingKeyUpdated;

    @Value("${rabbitmq.routing-key.deleted}")
    private String routingKeyDeleted;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue queueCreated() {
        return QueueBuilder.durable(queueCreated).build();
    }

    @Bean
    public Queue queueUpdated() {
        return QueueBuilder.durable(queueUpdated).build();
    }

    @Bean
    public Queue queueDeleted() {
        return QueueBuilder.durable(queueDeleted).build();
    }

    @Bean
    public Binding bindingCreated() {
        return BindingBuilder
                .bind(queueCreated())
                .to(exchange())
                .with(routingKeyCreated);
    }

    @Bean
    public Binding bindingUpdated() {
        return BindingBuilder
                .bind(queueUpdated())
                .to(exchange())
                .with(routingKeyUpdated);
    }

    @Bean
    public Binding bindingDeleted() {
        return BindingBuilder
                .bind(queueDeleted())
                .to(exchange())
                .with(routingKeyDeleted);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
