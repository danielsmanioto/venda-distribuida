package com.vendadistribuida.vendas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.produtos.url}")
    private String produtosServiceUrl;

    @Bean
    public WebClient produtosWebClient() {
        return WebClient.builder()
                .baseUrl(produtosServiceUrl)
                .build();
    }
}
