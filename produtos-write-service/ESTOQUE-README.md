# Feature: Controle de Estoque

## Descrição
Sistema de controle de movimentações de estoque para produtos, incluindo entradas, saídas, consulta de saldo e histórico de movimentações.

## Componentes Implementados

### 1. Modelo de Dados

#### Entidade: MovimentacaoEstoque
- **Tabela**: `movimentacao_estoque`
- **Campos**:
  - `id`: Identificador único (auto-incremento)
  - `produto_id`: Referência ao produto (FK)
  - `tipo`: ENTRADA ou SAIDA
  - `quantidade`: Quantidade movimentada (sempre positivo)
  - `motivo`: Descrição opcional da movimentação
  - `criado_em`: Data/hora de criação (automático)

#### Atualização: Produto
- Adicionado campo `quantidade_estoque` para armazenar o saldo atual

### 2. Endpoints Implementados

#### POST /produtos/{id}/estoque/entrada
Registra uma entrada de estoque.

**Request:**
```json
{
  "quantidade": 50,
  "motivo": "Compra fornecedor X"
}
```

**Response 200:**
```json
{
  "produtoId": 1,
  "saldoAnterior": 10,
  "quantidadeAdicionada": 50,
  "saldoAtual": 60,
  "criadoEm": "2026-03-04T10:00:00"
}
```

**Erros:**
- `404`: Produto não encontrado
- `400`: Quantidade inválida (zero ou negativa)

---

#### POST /produtos/{id}/estoque/saida
Registra uma saída de estoque.

**Request:**
```json
{
  "quantidade": 5,
  "motivo": "Venda pedido #42"
}
```

**Response 200:**
```json
{
  "produtoId": 1,
  "saldoAnterior": 60,
  "quantidadeSaida": 5,
  "saldoAtual": 55,
  "criadoEm": "2026-03-04T10:05:00"
}
```

**Erros:**
- `404`: Produto não encontrado
- `422`: Estoque insuficiente
- `400`: Quantidade inválida

---

#### GET /produtos/{id}/estoque/saldo
Consulta o saldo atual do estoque.

**Response 200:**
```json
{
  "produtoId": 1,
  "nomeProduto": "Teclado Mecânico",
  "saldoAtual": 55
}
```

**Erros:**
- `404`: Produto não encontrado

---

#### GET /produtos/{id}/estoque/historico
Retorna o histórico de movimentações (paginado).

**Query Parameters:**
- `tipo` (opcional): `ENTRADA` ou `SAIDA`
- `dataInicio` (opcional): formato `yyyy-MM-dd`
- `dataFim` (opcional): formato `yyyy-MM-dd`
- `page` (opcional): número da página (padrão: 0)
- `size` (opcional): itens por página (padrão: 20)

**Exemplo:**
```
GET /produtos/1/estoque/historico?tipo=SAIDA&dataInicio=2026-01-01&page=0&size=10
```

**Response 200:**
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

**Erros:**
- `404`: Produto não encontrado

### 3. Regras de Negócio

✅ Quantidade deve ser sempre maior que zero (validado com Bean Validation)
✅ Saída só é permitida se `saldoAtual >= quantidade` solicitada
✅ Entrada incrementa o `produto.quantidadeEstoque`
✅ Saída decrementa o `produto.quantidadeEstoque`
✅ Atualização do saldo e registro da movimentação ocorrem na mesma transação (`@Transactional`)
✅ `criadoEm` é gerado automaticamente pelo banco
✅ `motivo` é opcional

### 4. Estrutura de Classes

```
📁 controller/
  └── EstoqueController.java

📁 service/
  └── EstoqueService.java

📁 repository/
  └── MovimentacaoRepository.java

📁 domain/
  ├── entity/
  │   └── MovimentacaoEstoque.java
  ├── enums/
  │   └── TipoMovimentacao.java
  └── dto/
      ├── MovimentacaoEstoqueRequest.java
      ├── EntradaEstoqueResponse.java
      ├── SaidaEstoqueResponse.java
      ├── SaldoEstoqueResponse.java
      ├── HistoricoEstoqueResponse.java
      └── MovimentacaoDto.java

📁 exception/
  ├── ProdutoNotFoundException.java
  └── EstoqueInsuficienteException.java
```

### 5. Testes

Arquivo de testes: `EstoqueServiceTest.java`

**Casos de teste implementados:**
- ✅ Entrada válida incrementa saldo corretamente
- ✅ Entrada em produto inexistente retorna 404
- ✅ Saída válida decrementa saldo corretamente
- ✅ Saída com estoque insuficiente retorna 422
- ✅ Saída em produto inexistente retorna 404
- ✅ Consulta de saldo retorna valores corretos
- ✅ Consulta de saldo de produto inexistente retorna 404
- ✅ Histórico retorna paginado corretamente
- ✅ Filtro por tipo funciona
- ✅ Produto sem movimentações retorna lista vazia (não 404)

### 6. Migration SQL

Arquivo: `V002__criar_tabela_movimentacao_estoque.sql`

- Cria tabela `movimentacao_estoque`
- Adiciona índices para otimização de consultas
- Adiciona coluna `quantidade_estoque` na tabela `produtos` (se não existir)

## Como Executar

1. **Build do projeto:**
```bash
cd produtos-write-service
./mvnw clean package
```

2. **Executar testes:**
```bash
./mvnw test
```

3. **Executar aplicação:**
```bash
./mvnw spring-boot:run
```

## Exemplos de Uso

### Registrar entrada de 100 unidades
```bash
curl -X POST http://localhost:8081/produtos/1/estoque/entrada \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": 100,
    "motivo": "Compra fornecedor ABC"
  }'
```

### Registrar saída de 5 unidades
```bash
curl -X POST http://localhost:8081/produtos/1/estoque/saida \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": 5,
    "motivo": "Venda pedido #123"
  }'
```

### Consultar saldo atual
```bash
curl http://localhost:8081/produtos/1/estoque/saldo
```

### Consultar histórico com filtros
```bash
curl "http://localhost:8081/produtos/1/estoque/historico?tipo=SAIDA&dataInicio=2026-01-01&dataFim=2026-12-31&page=0&size=10"
```

## Observações

- A feature está totalmente integrada com o sistema de log e observabilidade existente
- Todas as operações são transacionais, garantindo consistência dos dados
- O sistema valida automaticamente os dados de entrada usando Bean Validation
- As exceções são tratadas pelo `GlobalExceptionHandler` com respostas padronizadas
- Os índices no banco de dados garantem performance nas consultas de histórico
