package com.vendadistribuida.vendas.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequest {

    @NotNull(message = "Usuário ID é obrigatório")
    private Long usuarioId;

    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid
    private List<ItemVendaRequest> itens;
}
