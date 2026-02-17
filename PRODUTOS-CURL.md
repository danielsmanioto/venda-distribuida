# Produtos Service - cURL Examples

## 📝 WRITE SERVICE (Port 8081)

### 1️⃣ CRIAR PRODUTO
```bash
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook Dell",
    "descricao": "Notebook 15 polegadas, Intel i7",
    "preco": 4500.00,
    "estoque": 50,
    "sku": "NOTEBOOK-DELL-001"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "Notebook Dell",
  "descricao": "Notebook 15 polegadas, Intel i7",
  "preco": 4500.00,
  "estoque": 50,
  "sku": "NOTEBOOK-DELL-001",
  "criadoEm": "2026-02-17T10:30:00Z"
}
```

---

### 2️⃣ ATUALIZAR PRODUTO
```bash
curl -X PUT http://localhost:8081/api/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook Dell XPS",
    "descricao": "Notebook 15 polegadas, Intel i7, 16GB RAM",
    "preco": 5200.00,
    "estoque": 45,
    "sku": "NOTEBOOK-DELL-XPS-001"
  }'
```

---

### 3️⃣ ATUALIZAR ESTOQUE
```bash
curl -X PATCH http://localhost:8081/api/produtos/1/estoque \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": -5,
    "motivo": "Venda"
  }'
```

---

### 4️⃣ DELETAR PRODUTO
```bash
curl -X DELETE http://localhost:8081/api/produtos/1
```

---

## 🔍 READ SERVICE (Port 8082)

### 1️⃣ LISTAR TODOS OS PRODUTOS (com cache)
```bash
curl -X GET http://localhost:8082/api/produtos
```

**Resposta esperada:**
```json
[
  {
    "id": 1,
    "nome": "Notebook Dell",
    "descricao": "Notebook 15 polegadas, Intel i7",
    "preco": 4500.00,
    "estoque": 50,
    "sku": "NOTEBOOK-DELL-001"
  },
  {
    "id": 2,
    "nome": "Mouse Logitech",
    "descricao": "Mouse wireless",
    "preco": 150.00,
    "estoque": 200,
    "sku": "MOUSE-LOG-001"
  }
]
```

---

### 2️⃣ BUSCAR PRODUTO POR ID
```bash
curl -X GET http://localhost:8082/api/produtos/1
```

---

### 3️⃣ BUSCAR POR SKU
```bash
curl -X GET http://localhost:8082/api/produtos/sku/NOTEBOOK-DELL-001
```

---

### 4️⃣ LISTAR POR CATEGORIA
```bash
curl -X GET "http://localhost:8082/api/produtos?categoria=eletrônicos"
```

---

### 5️⃣ BUSCAR COM PAGINAÇÃO
```bash
# Página 1, 10 itens por página
curl -X GET "http://localhost:8082/api/produtos?page=0&size=10&sort=nome,asc"
```

---

### 6️⃣ BUSCA FULL TEXT
```bash
curl -X GET "http://localhost:8082/api/produtos/busca?q=notebook"
```

---

## 🔄 FLUXO COMPLETO

### Setup de dados
```bash
# Criar alguns produtos
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teclado Mecânico",
    "descricao": "Teclado RGB, switches cherry",
    "preco": 450.00,
    "estoque": 30,
    "sku": "TECLADO-MECANICO-001"
  }'

curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Monitor LG 27\"",
    "descricao": "Monitor 4K, 60Hz",
    "preco": 1800.00,
    "estoque": 15,
    "sku": "MONITOR-LG-27"
  }'
```

### Listar via read service (com cache)
```bash
# Primeira requisição (do DB)
curl -X GET http://localhost:8082/api/produtos

# Próximas requisições (do cache Redis)
curl -X GET http://localhost:8082/api/produtos
```

### Atualizar estoque
```bash
# Diminuir estoque (venda)
curl -X PATCH http://localhost:8081/api/produtos/1/estoque \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": -2,
    "motivo": "Venda ID 123"
  }'

# Aumentar estoque (devolução)
curl -X PATCH http://localhost:8081/api/produtos/1/estoque \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": 1,
    "motivo": "Devolução"
  }'
```

---

## 📊 CASOS DE USO

### Buscar produtos baratos
```bash
curl -X GET "http://localhost:8082/api/produtos/busca?minPrice=0&maxPrice=500"
```

### Produtos em falta de estoque
```bash
curl -X GET "http://localhost:8082/api/produtos/baixo-estoque"
```

### Top produtos mais vendidos
```bash
curl -X GET "http://localhost:8082/api/produtos/top-vendas?limit=5"
```

---

## 🔐 Validações e Erros

### Erro: Produto não encontrado
```bash
curl -X GET http://localhost:8082/api/produtos/999
```
**Resposta:**
```json
{
  "error": "Produto não encontrado",
  "status": 404
}
```

### Erro: Estoque insuficiente
```bash
curl -X PATCH http://localhost:8081/api/produtos/1/estoque \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": -1000,
    "motivo": "Teste"
  }'
```
**Resposta:**
```json
{
  "error": "Estoque insuficiente",
  "status": 400
}
```

### Erro: SKU duplicado
```bash
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Outro Notebook",
    "descricao": "...",
    "preco": 3000.00,
    "estoque": 10,
    "sku": "NOTEBOOK-DELL-001"
  }'
```
**Resposta:**
```json
{
  "error": "SKU já existe",
  "status": 409
}
```

---

## 💡 DICAS

### Monitorar cache (Redis Commander)
```
Acesse: http://localhost:8081 (Redis Commander)
```

### Performance: Comparar tempos
```bash
# Primeira chamada (slow - DB)
time curl -s http://localhost:8082/api/produtos > /dev/null

# Próxima chamada (fast - cache)
time curl -s http://localhost:8082/api/produtos > /dev/null
```

### Forçar refresh de cache
```bash
# DELETE via admin endpoint
curl -X DELETE http://localhost:8082/api/cache/invalidate \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

---

## ✅ Health Checks

```bash
# Write Service
curl http://localhost:8081/actuator/health

# Read Service  
curl http://localhost:8082/actuator/health
```
