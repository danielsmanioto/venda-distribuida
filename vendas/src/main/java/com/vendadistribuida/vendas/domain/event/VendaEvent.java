package com.vendadistribuida.vendas.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendaEvent implements Serializable {

    private Long vendaId;
    private Long usuarioId;
    private BigDecimal valorTotal;
    private String status;
    private List<ItemVendaEvent> itens;
    private LocalDateTime timestamp;
    private EventType eventType;

    public enum EventType {
        CRIADA, PROCESSADA, CANCELADA
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVendaEvent implements Serializable {
        private Long produtoId;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoUnitario;
    }
}
