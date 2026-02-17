# API Testing - Venda Distribuída

Coleção de requisições para testar todos os endpoints dos microserviços.

## 🔐 1. Autenticação (usuarios-service - :8080)

### Registrar Usuário
```bash
POST http://localhost:8080/api/auth/registro
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "role": "USER"
}
```

### Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

> ⚠️ Copie o token para usar nas próximas requisições!

### Listar Usuários
```bash
GET http://localhost:8080/api/usuarios
Authorization: Bearer SEU_TOKEN_AQUI
```

### Buscar Usuário por ID
```bash
GET http://localhost:8080/api/usuarios/1
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 📦 2. Produtos - Write (produtos-write-service - :8081)

### Criar Produto
```bash
POST http://localhost:8081/api/produtos
Content-Type: application/json
Authorization: Bearer SEU_TOKEN_AQUI

{
  "nome": "Notebook Dell Inspiron",
  "descricao": "Notebook Dell i7 16GB RAM 512GB SSD",
  "preco": 3500.00,
  "estoque": 10,
  "categoria": "Eletrônicos",
  "sku": "DELL-INS-001"
}
```

### Atualizar Produto
```bash
PUT http://localhost:8081/api/produtos/1
Content-Type: application/json
Authorization: Bearer SEU_TOKEN_AQUI

{
  "nome": "Notebook Dell Inspiron 15",
  "descricao": "Notebook Dell i7 16GB RAM 512GB SSD - Atualizado",
  "preco": 3299.00,
  "estoque": 15,
  "categoria": "Eletrônicos",
  "sku": "DELL-INS-001"
}
```

### Atualizar Estoque
```bash
PATCH http://localhost:8081/api/produtos/1/estoque
Content-Type: application/json
Authorization: Bearer SEU_TOKEN_AQUI

{
  "quantidade": 20
}
```

### Deletar Produto
```bash
DELETE http://localhost:8081/api/produtos/1
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 📖 3. Produtos - Read (produtos-read-service - :8082)

### Listar Todos os Produtos
```bash
GET http://localhost:8082/api/produtos
```

### Buscar Produto por ID
```bash
GET http://localhost:8082/api/produtos/1
```

### Buscar por Categoria
```bash
GET http://localhost:8082/api/produtos/categoria/Eletrônicos
```

### Buscar por SKU
```bash
GET http://localhost:8082/api/produtos/sku/DELL-INS-001
```

### Buscar (Search)
```bash
GET http://localhost:8082/api/produtos/buscar?termo=dell
```

---

## 🛒 4. Vendas (vendas-service - :8083)

### Criar Venda
```bash
POST http://localhost:8083/api/vendas
Content-Type: application/json
Authorization: Bearer SEU_TOKEN_AQUI

{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    },
    {
      "produtoId": 2,
      "quantidade": 1
    }
  ]
}
```

### Buscar Venda por ID
```bash
GET http://localhost:8083/api/vendas/1
Authorization: Bearer SEU_TOKEN_AQUI
```

### Buscar Vendas do Usuário
```bash
GET http://localhost:8083/api/vendas/usuario/1
Authorization: Bearer SEU_TOKEN_AQUI
```

### Listar Vendas (Paginado)
```bash
GET http://localhost:8083/api/vendas?page=0&size=20&sortBy=id&direction=DESC
Authorization: Bearer SEU_TOKEN_AQUI
```

### Cancelar Venda
```bash
PUT http://localhost:8083/api/vendas/1/cancelar
Authorization: Bearer SEU_TOKEN_AQUI
```

---

## 🏥 5. Health Checks

### usuarios-service
```bash
GET http://localhost:8080/actuator/health
```

### produtos-write-service
```bash
GET http://localhost:8081/actuator/health
```

### produtos-read-service
```bash
GET http://localhost:8082/actuator/health
```

### vendas-service
```bash
GET http://localhost:8083/actuator/health
```

---

## 📊 6. Métricas (Prometheus)

### usuarios-service
```bash
GET http://localhost:8080/actuator/prometheus
```

### produtos-write-service
```bash
GET http://localhost:8081/actuator/prometheus
```

### produtos-read-service
```bash
GET http://localhost:8082/actuator/prometheus
```

### vendas-service
```bash
GET http://localhost:8083/actuator/prometheus
```

---

## 🧪 Cenários de Teste Completos

### Cenário 1: Fluxo Completo de Venda

1. **Criar usuário**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "senha": "senha123",
    "role": "USER"
  }'
```

2. **Fazer login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@email.com",
    "senha": "senha123"
  }'
```

3. **Criar produto (Write)**
```bash
TOKEN="SEU_TOKEN_AQUI"

curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Mouse Logitech MX Master",
    "descricao": "Mouse ergonômico sem fio",
    "preco": 399.00,
    "estoque": 50,
    "categoria": "Periféricos",
    "sku": "LOG-MX-001"
  }'
```

4. **Listar produtos (Read - com cache)**
```bash
curl http://localhost:8082/api/produtos
```

5. **Criar venda**
```bash
curl -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "itens": [
      {
        "produtoId": 1,
        "quantidade": 2
      }
    ]
  }'
```

6. **Buscar venda criada**
```bash
curl http://localhost:8083/api/vendas/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Cenário 2: Testar CQRS e Cache

1. **Criar produto no Write**
```bash
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Teclado Mecânico",
    "preco": 450.00,
    "estoque": 30,
    "categoria": "Periféricos",
    "sku": "TEC-MEC-001"
  }'
```

2. **Buscar no Read (1ª vez - sem cache)**
```bash
curl http://localhost:8082/api/produtos/2 -w "\nTempo: %{time_total}s\n"
```

3. **Buscar no Read (2ª vez - com cache)**
```bash
curl http://localhost:8082/api/produtos/2 -w "\nTempo: %{time_total}s\n"
```

4. **Atualizar produto no Write**
```bash
curl -X PUT http://localhost:8081/api/produtos/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Teclado Mecânico RGB",
    "preco": 499.00,
    "estoque": 35,
    "categoria": "Periféricos",
    "sku": "TEC-MEC-001"
  }'
```

5. **Verificar cache invalidado**
```bash
curl http://localhost:8082/api/produtos/2
# Cache foi invalidado, busca do banco
```

### Cenário 3: Testar Circuit Breaker

1. **Parar produtos-read-service** (Ctrl+C no terminal)

2. **Tentar criar venda** (vai falhar após 3 tentativas)
```bash
curl -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "usuarioId": 1,
    "itens": [{"produtoId": 1, "quantidade": 1}]
  }'
```

3. **Reiniciar produtos-read-service**

4. **Tentar novamente** (deve funcionar após circuit breaker fechar)

---

## 🐞 Debug

### Verificar RabbitMQ

```bash
# Acessar UI
open http://localhost:15672
# Login: admin / admin123

# Verificar mensagens na fila
# Exchanges → produto.events
# Queues → produto.read.queue
```

### Verificar Kafka

```bash
# Listar tópicos
docker exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Consumir mensagens
docker exec kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic venda.criada \
  --from-beginning
```

### Verificar Redis Cache

```bash
# Conectar ao Redis
docker exec -it redis redis-cli -a redis123

# Listar chaves
KEYS *

# Ver valor
GET produtos::1

# Limpar cache
FLUSHALL
```

### Ver Logs

```bash
# Docker logs
docker logs redis -f
docker logs rabbitmq -f
docker logs kafka -f

# Logs dos microserviços aparecem no terminal onde foram iniciados
```

---

## 📦 Coleção Postman/Insomnia

Importe este arquivo no Postman ou Insomnia para testes mais fáceis.

**Variáveis de ambiente:**
- `baseUrl`: http://localhost
- `token`: (será preenchido após login)
- `usuariosPort`: 8080
- `produtosWritePort`: 8081
- `produtosReadPort`: 8082
- `vendasPort`: 8083
