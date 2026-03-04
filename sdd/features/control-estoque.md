## FEATURE: Controle de Estoque

### Contexto
Projeto `produtos-write` em Java (Spring Boot) com PostgreSQL.
A entidade `Produto` já existe. Esta feature adiciona controle de movimentações
de estoque (entradas e saídas), consulta de saldo atual e histórico de movimentações.

---

### Modelo de Dados

#### Entidade existente (referência)
```java
// Produto já existe — apenas adicionar o campo abaixo se ainda não tiver
Produto {
  Long id;
  String nome;
  // ... demais campos existentes
  Integer quantidadeEstoque; // saldo atual — adicionar se não existir
}
```

#### Nova entidade: MovimentacaoEstoque
```java
MovimentacaoEstoque {
  Long id;
  Long produtoId;          // FK para Produto
  TipoMovimentacao tipo;   // ENTRADA | SAIDA
  Integer quantidade;      // sempre positivo
  String motivo;           // ex: "Compra fornecedor X", "Venda pedido #42"
  LocalDateTime criadoEm;  // gerado automaticamente
}

enum TipoMovimentacao {
  ENTRADA,
  SAIDA
}
```

#### Tabela PostgreSQL
```sql
CREATE TABLE movimentacao_estoque (
  id           BIGSERIAL PRIMARY KEY,
  produto_id   BIGINT NOT NULL REFERENCES produto(id),
  tipo         VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA')),
  quantidade   INTEGER NOT NULL CHECK (quantidade > 0),
  motivo       VARCHAR(255),
  criado_em    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimentacao_produto_id ON movimentacao_estoque(produto_id);
CREATE INDEX idx_movimentacao_criado_em  ON movimentacao_estoque(criado_em);
```

---

### Endpoints

#### POST /produtos/{id}/estoque/entrada
Registra uma entrada de estoque para o produto.

- **Input:**
```json
{
  "quantidade": 50,
  "motivo": "Compra fornecedor X"
}
```

- **Output sucesso (200):**
```json
{
  "produtoId": 1,
  "saldoAnterior": 10,
  "quantidadeAdicionada": 50,
  "saldoAtual": 60,
  "criadoEm": "2026-03-04T10:00:00"
}
```

- **Output erro (404):** produto não encontrado
```json
{ "erro": "Produto não encontrado" }
```

- **Output erro (400):** quantidade inválida
```json
{ "erro": "Quantidade deve ser maior que zero" }
```

---

#### POST /produtos/{id}/estoque/saida
Registra uma saída de estoque para o produto.

- **Input:**
```json
{
  "quantidade": 5,
  "motivo": "Venda pedido #42"
}
```

- **Output sucesso (200):**
```json
{
  "produtoId": 1,
  "saldoAnterior": 60,
  "quantidadeSaida": 5,
  "saldoAtual": 55,
  "criadoEm": "2026-03-04T10:05:00"
}
```

- **Output erro (404):** produto não encontrado
```json
{ "erro": "Produto não encontrado" }
```

- **Output erro (422):** estoque insuficiente
```json
{
  "erro": "Estoque insuficiente",
  "saldoAtual": 3,
  "quantidadeSolicitada": 5
}
```

- **Output erro (400):** quantidade inválida
```json
{ "erro": "Quantidade deve ser maior que zero" }
```

---

#### GET /produtos/{id}/estoque/saldo
Consulta o saldo atual do produto.

- **Output sucesso (200):**
```json
{
  "produtoId": 1,
  "nomeProduto": "Teclado Mecânico",
  "saldoAtual": 55
}
```

- **Output erro (404):**
```json
{ "erro": "Produto não encontrado" }
```

---

#### GET /produtos/{id}/estoque/historico
Retorna o histórico de movimentações do produto.

- **Query params opcionais:**
  - `tipo` → `ENTRADA` | `SAIDA`
  - `dataInicio` → `yyyy-MM-dd`
  - `dataFim` → `yyyy-MM-dd`
  - `page` → padrão `0`
  - `size` → padrão `20`

- **Exemplo de request:**
```
GET /produtos/1/estoque/historico?tipo=SAIDA&dataInicio=2026-01-01&page=0&size=10
```

- **Output sucesso (200):**
```json
{
  "produtoId": 1,
  "totalMovimentacoes": 42,
  "page": 0,
  "size": 10,
  "movimentacoes": [
    {
      "id": 10,
      "tipo": "SAIDA",
      "quantidade": 5,
      "motivo": "Venda pedido #42",
      "criadoEm": "2026-03-04T10:05:00"
    }
  ]
}
```

---

### Regras de Negócio

- `quantidade` deve ser sempre maior que zero (entrada e saída)
- Saída só é permitida se `saldoAtual >= quantidade` solicitada
- Ao registrar entrada → incrementa `produto.quantidadeEstoque`
- Ao registrar saída  → decrementa `produto.quantidadeEstoque`
- A atualização do saldo e o registro da movimentação devem ocorrer na **mesma transação** (`@Transactional`)
- `criadoEm` é gerado automaticamente pelo banco — não aceitar no input
- `motivo` é opcional mas recomendado

---

### Camadas esperadas

```
EstoqueController      →  recebe requisição, valida input (Bean Validation)
EstoqueService         →  regras de negócio + @Transactional
MovimentacaoRepository →  JPA Repository para MovimentacaoEstoque
ProdutoRepository      →  já existe — usar para buscar e atualizar saldo
```

---

### Casos de Teste Esperados

#### Entrada
- ✅ Entrada válida incrementa saldo corretamente
- ✅ Entrada com quantidade zero retorna 400
- ✅ Entrada com quantidade negativa retorna 400
- ✅ Entrada em produto inexistente retorna 404

#### Saída
- ✅ Saída válida decrementa saldo corretamente
- ✅ Saída com estoque insuficiente retorna 422
- ✅ Saída com quantidade zero retorna 400
- ✅ Saída em produto inexistente retorna 404

#### Saldo
- ✅ Saldo retorna valor correto após movimentações
- ✅ Produto inexistente retorna 404

#### Histórico
- ✅ Retorna paginado corretamente
- ✅ Filtro por tipo funciona
- ✅ Filtro por data funciona
- ✅ Produto sem movimentações retorna lista vazia (não 404)

#### Transação
- ✅ Se falhar ao salvar movimentação, saldo NÃO é alterado
- ✅ Se falhar ao atualizar saldo, movimentação NÃO é salva

---

### Fora do Escopo
- Cadastro e edição de produtos (já existe)
- Estoque mínimo / alertas de reposição (feature futura)
- Integração com pedidos/vendas (feature futura)
- Multi-warehouse / múltiplos almoxarifados
- Soft delete de movimentações (movimentação é imutável por design)