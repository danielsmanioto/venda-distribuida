package com.vendadistribuida.produtos.write.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntradaEstoqueResponse {

    private Long produtoId;
    private Integer saldoAnterior;
    private Integer quantidadeAdicionada;
    private Integer saldoAtual;
    private LocalDateTime criadoEm;
}
