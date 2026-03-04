package com.vendadistribuida.produtos.write.domain.dto;

import com.vendadistribuida.produtos.write.domain.enums.TipoMovimentacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoDto {

    private Long id;
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private String motivo;
    private LocalDateTime criadoEm;
}
