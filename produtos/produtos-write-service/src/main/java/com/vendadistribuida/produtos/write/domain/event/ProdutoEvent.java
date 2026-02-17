package com.vendadistribuida.produtos.write.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoEvent {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
    private String categoria;
    private String sku;
    private String imagemUrl;
    private Boolean ativo;
    private LocalDateTime timestamp;
    private EventType eventType;

    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}
