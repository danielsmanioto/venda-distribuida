package com.vendadistribuida.produtos.write.exception;

import lombok.Getter;

@Getter
public class EstoqueInsuficienteException extends RuntimeException {

    private final Integer saldoAtual;
    private final Integer quantidadeSolicitada;

    public EstoqueInsuficienteException(Integer saldoAtual, Integer quantidadeSolicitada) {
        super("Estoque insuficiente");
        this.saldoAtual = saldoAtual;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }
}
