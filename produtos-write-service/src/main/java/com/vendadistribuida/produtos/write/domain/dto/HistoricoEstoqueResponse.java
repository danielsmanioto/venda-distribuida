package com.vendadistribuida.produtos.write.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEstoqueResponse {

    private Long produtoId;
    private Long totalMovimentacoes;
    private Integer page;
    private Integer size;
    private List<MovimentacaoDto> movimentacoes;
}
