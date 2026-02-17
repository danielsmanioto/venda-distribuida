package com.vendadistribuida.vendas.domain.dto;

import com.vendadistribuida.vendas.domain.entity.Venda;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponse {

    private Long id;
    private Long usuarioId;
    private BigDecimal valorTotal;
    private Venda.StatusVenda status;
    private List<ItemVendaResponse> itens;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
