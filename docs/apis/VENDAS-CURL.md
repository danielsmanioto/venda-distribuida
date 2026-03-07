# Vendas Service - cURL Examples

## 1️⃣ CRIAR VENDA
```bash
# Precisa de um token JWT válido (do serviço de usuários)
TOKEN="seu_jwt_token_aqui"

curl -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "produtos": [
      {
        "produtoId": 1,
        "quantidade": 2,
        "precoUnitario": 4500.00
      },
      {
        "produtoId": 2,
        "quantidade": 1,
        "precoUnitario": 150.00
      }
    ],
    "desconto": 100.00
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "usuarioId": 1,
  "status": "PENDENTE",
  "total": 9150.00,
  "desconto": 100.00,
  "items": [
    {
      "produtoId": 1,
      "quantidade": 2,
      "precoUnitario": 4500.00
    },
    {
      "produtoId": 2,
      "quantidade": 1,
      "precoUnitario": 150.00
    }
  ],
  "criadoEm": "2026-02-17T10:30:00Z",
  "processadoEm": null
}
```

---

## 2️⃣ LISTAR VENDAS DO USUÁRIO
```bash
TOKEN="seu_jwt_token_aqui"

# Listar minhas vendas
curl -X GET http://localhost:8083/api/vendas/minha \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "status": "PROCESSADA",
    "total": 9050.00,
    "desconto": 100.00,
    "criadoEm": "2026-02-17T10:30:00Z",
    "processadoEm": "2026-02-17T10:35:00Z"
  },
  {
    "id": 2,
    "usuarioId": 1,
    "status": "PENDENTE",
    "total": 500.00,
    "desconto": 0.00,
    "criadoEm": "2026-02-17T11:00:00Z",
    "processadoEm": null
  }
]
```

---

## 3️⃣ BUSCAR VENDA POR ID
```bash
TOKEN="seu_jwt_token_aqui"

curl -X GET http://localhost:8083/api/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4️⃣ LISTAR VENDAS COM PAGINAÇÃO
```bash
TOKEN="seu_jwt_token_aqui"

# Página 0, 10 items, ordenado por data descente
curl -X GET "http://localhost:8083/api/vendas?page=0&size=10&sort=criadoEm,desc" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5️⃣ CANCELAR VENDA
```bash
TOKEN="seu_jwt_token_aqui"

curl -X DELETE http://localhost:8083/api/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
```json
{
  "id": 1,
  "usuarioId": 1,
  "status": "CANCELADA",
  "total": 9050.00,
  "desconto": 100.00,
  "canceladoEm": "2026-02-17T10:45:00Z"
}
```

---

## 6️⃣ LISTAR VENDAS (ADMIN)
```bash
TOKEN="seu_jwt_token_admin_aqui"

# Ver todas as vendas do sistema
curl -X GET http://localhost:8083/api/vendas/admin/todas \
  -H "Authorization: Bearer $TOKEN"
```

---

## 7️⃣ FILTRAR VENDAS POR STATUS
```bash
TOKEN="seu_jwt_token_aqui"

# Status: PENDENTE, PROCESSADA, CANCELADA
curl -X GET "http://localhost:8083/api/vendas?status=PROCESSADA" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 8️⃣ FILTRAR POR DATA
```bash
TOKEN="seu_jwt_token_aqui"

# Vendas entre duas datas
curl -X GET "http://localhost:8083/api/vendas?dataInicio=2026-02-01&dataFim=2026-02-28" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔄 FLUXO COMPLETO

### Passo 1: Fazer Login
```bash
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "senha": "Senha123!"
  }')

TOKEN=$(echo $RESPONSE | jq -r '.token')
USUARIO_ID=$(echo $RESPONSE | jq -r '.usuario.id')

echo "Token: $TOKEN"
echo "Usuario ID: $USUARIO_ID"
```

### Passo 2: Criar Venda
```bash
VENDA=$(curl -s -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": '$USUARIO_ID',
    "produtos": [
      {
        "produtoId": 1,
        "quantidade": 2,
        "precoUnitario": 4500.00
      }
    ],
    "desconto": 0.00
  }')

VENDA_ID=$(echo $VENDA | jq -r '.id')
echo "Venda criada: $VENDA_ID"
```

### Passo 3: Verificar Status
```bash
curl -X GET http://localhost:8083/api/vendas/$VENDA_ID \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Passo 4: Listar Minhas Vendas
```bash
curl -X GET http://localhost:8083/api/vendas/minha \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Passo 5: Cancelar se necessário
```bash
curl -X DELETE http://localhost:8083/api/vendas/$VENDA_ID \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## 📊 CASOS DE USO

### Vendas do mês
```bash
curl -X GET "http://localhost:8083/api/vendas?dataInicio=2026-02-01&dataFim=2026-02-28" \
  -H "Authorization: Bearer $TOKEN"
```

### Total gasto pelo usuário
```bash
curl -s -X GET http://localhost:8083/api/vendas/minha \
  -H "Authorization: Bearer $TOKEN" | jq '[.[].total] | add'
```

### Vendas pendentes de processamento
```bash
curl -X GET "http://localhost:8083/api/vendas?status=PENDENTE" \
  -H "Authorization: Bearer $TOKEN"
```

### Maior venda
```bash
curl -s -X GET http://localhost:8083/api/vendas/minha \
  -H "Authorization: Bearer $TOKEN" | jq 'max_by(.total)'
```

---

## 🔐 Validações e Erros

### Erro: Produto não encontrado
```bash
curl -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "produtos": [
      {
        "produtoId": 999,
        "quantidade": 1,
        "precoUnitario": 100.00
      }
    ],
    "desconto": 0.00
  }'
```
**Resposta:**
```json
{
  "error": "Produto não encontrado",
  "status": 404
}
```

### Erro: Estoque insuficiente
```json
{
  "error": "Estoque insuficiente para produto ID 1. Disponível: 5, Solicitado: 10",
  "status": 400
}
```

### Erro: Venda não encontrada
```bash
curl -X GET http://localhost:8083/api/vendas/999 \
  -H "Authorization: Bearer $TOKEN"
```
**Resposta:**
```json
{
  "error": "Venda não encontrada",
  "status": 404
}
```

### Erro: Venda já foi processada (não pode cancelar)
```bash
curl -X DELETE http://localhost:8083/api/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```
**Resposta:**
```json
{
  "error": "Não é possível cancelar uma venda processada",
  "status": 400
}
```

### Erro: Sem autorização (não é seu próprio usuário)
```bash
curl -X GET http://localhost:8083/api/vendas/999 \
  -H "Authorization: Bearer $TOKEN"
```
**Resposta:**
```json
{
  "error": "Você não tem permissão para acessar esta venda",
  "status": 403
}
```

---

## 🔄 INTEGRAÇÃO COM KAFKA

Quando uma venda é criada, os seguintes eventos são publicados:

### Event: venda.created
```json
{
  "vendaId": 1,
  "usuarioId": 1,
  "total": 9050.00,
  "itens": 2,
  "timestamp": "2026-02-17T10:30:00Z"
}
```

### Event: venda.processada
```json
{
  "vendaId": 1,
  "usuarioId": 1,
  "total": 9050.00,
  "processadoEm": "2026-02-17T10:35:00Z"
}
```

### Event: venda.cancelada
```json
{
  "vendaId": 1,
  "usuarioId": 1,
  "canceladoEm": "2026-02-17T10:45:00Z",
  "motivo": "Cancelado pelo usuário"
}
```

---

## 💡 DICAS

### Monitorar Kafka
```bash
# Dentro do container
docker exec kafka bash -c 'kafka-console-consumer --bootstrap-server localhost:9092 --topic venda.created --from-beginning'
```

### Simular múltiplas vendas
```bash
#!/bin/bash
for i in {1..10}; do
  curl -X POST http://localhost:8083/api/vendas \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{
      \"usuarioId\": 1,
      \"produtos\": [{
        \"produtoId\": $((RANDOM % 5 + 1)),
        \"quantidade\": $((RANDOM % 5 + 1)),
        \"precoUnitario\": $((RANDOM % 5000 + 100)).00
      }],
      \"desconto\": 0.00
    }"
  sleep 1
done
```

### Análise de dados
```bash
# Gasto total do usuário
curl -s -X GET http://localhost:8083/api/vendas/minha \
  -H "Authorization: Bearer $TOKEN" | \
  jq '[.[].total] | {total: add, media: (add/length), quantidade: length}'

# Respostas esperada:
# {"total": 15000.00, "media": 3000.00, "quantidade": 5}
```

---

## ✅ Health Check

```bash
curl http://localhost:8083/actuator/health | jq .
```
