package com.vendadistribuida.vendas.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO implements Serializable {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;
    private Boolean ativo;
}
