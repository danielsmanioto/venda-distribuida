package com.vendadistribuida.produtos.write.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoEstoqueResponse {

    private Long produtoId;
    private String nomeProduto;
    private Integer saldoAtual;
}
