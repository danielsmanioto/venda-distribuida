package com.vendadistribuida.produtos.write.exception;

public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(String message) {
        super(message);
    }

    public ProdutoNotFoundException(Long id) {
        super("Produto não encontrado");
    }
}
