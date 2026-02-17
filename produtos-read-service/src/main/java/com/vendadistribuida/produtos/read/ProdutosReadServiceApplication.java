package com.vendadistribuida.produtos.read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProdutosReadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdutosReadServiceApplication.class, args);
    }
}
