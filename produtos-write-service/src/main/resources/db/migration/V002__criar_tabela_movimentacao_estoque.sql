-- Migration: Criar tabela movimentacao_estoque
-- Data: 2026-03-04
-- Descrição: Criação da tabela para controle de movimentações de estoque

CREATE TABLE IF NOT EXISTS movimentacao_estoque (
    id           BIGSERIAL PRIMARY KEY,
    produto_id   BIGINT NOT NULL,
    tipo         VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    quantidade   INTEGER NOT NULL CHECK (quantidade > 0),
    motivo       VARCHAR(255),
    criado_em    TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_movimentacao_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- Índices para melhorar performance de consultas
CREATE INDEX IF NOT EXISTS idx_movimentacao_produto_id ON movimentacao_estoque(produto_id);
CREATE INDEX IF NOT EXISTS idx_movimentacao_criado_em ON movimentacao_estoque(criado_em);

-- Adicionar coluna quantidade_estoque na tabela produtos (se não existir)
-- Comentar a linha abaixo se a coluna já existir
ALTER TABLE produtos ADD COLUMN IF NOT EXISTS quantidade_estoque INTEGER NOT NULL DEFAULT 0;

-- Migrar dados da coluna 'estoque' para 'quantidade_estoque' (se necessário)
-- UPDATE produtos SET quantidade_estoque = estoque WHERE quantidade_estoque = 0;
