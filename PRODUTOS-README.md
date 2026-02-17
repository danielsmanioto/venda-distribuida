# Produtos Services (CQRS)

Microserviços de gerenciamento de produtos com arquitetura CQRS (Command Query Responsibility Segregation).

## 🏗️ Arquitetura CQRS

Este módulo implementa o padrão CQRS dividindo as responsabilidades em:

- **produtos-write-service** (porta 8081): Operações de escrita (CREATE, UPDATE, DELETE)
- **produtos-read-service** (porta 8082): Operações de leitura (READ) com cache Redis

### Fluxo de Dados

```
┌─────────────────────────────────────────────────────────────┐
│                    WRITE SIDE                               │
│                                                             │
│  [Cliente] → [Write Service] → [PostgreSQL Master]         │
│                     ↓                                       │
│              [RabbitMQ Event]                              │
└─────────────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────────────┐
│                    READ SIDE                                │
│                                                             │
│  [RabbitMQ] → [Event Consumer] → [Cache Invalidation]     │
│                                                             │
│  [Cliente] → [Read Service] → [Redis Cache]               │
│                     ↓ (miss)                               │
│              [PostgreSQL Replica]                          │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **PostgreSQL** (Master-Replica)
- **Redis** (Cache)
- **RabbitMQ** (Event Streaming)
- **Resilience4j** (Circuit Breaker, Rate Limiter)
- **Micrometer** (Métricas e Tracing)

## 📁 Estrutura

```
venda-distribuida/
├── produtos-write-service/    # Serviço de escrita
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
└── produtos-read-service/     # Serviço de leitura
    ├── src/
    ├── Dockerfile
    └── pom.xml
```

---

## 📝 Write Service (Porta 8081)

### Funcionalidades

- ✅ Criar produtos
- ✅ Atualizar produtos
- ✅ Deletar produtos (soft delete)
- ✅ Atualizar estoque
- ✅ Publicar eventos para RabbitMQ
- ✅ Circuit Breaker
- ✅ Rate Limiting

### Endpoints

#### Criar Produto
```http
POST /api/produtos
Content-Type: application/json

{
  "nome": "Notebook Dell",
  "descricao": "Notebook Dell Inspiron 15",
  "preco": 3500.00,
  "estoque": 10,
  "categoria": "Eletrônicos",
  "sku": "DELL-INSP-15",
  "imagemUrl": "https://exemplo.com/imagem.jpg"
}
```

#### Atualizar Produto
```http
PUT /api/produtos/{id}
Content-Type: application/json

{
  "nome": "Notebook Dell Atualizado",
  "descricao": "Nova descrição",
  "preco": 3200.00,
  "estoque": 15,
  "categoria": "Eletrônicos",
  "sku": "DELL-INSP-15",
  "imagemUrl": "https://exemplo.com/nova-imagem.jpg"
}
```

#### Deletar Produto
```http
DELETE /api/produtos/{id}
```

#### Atualizar Estoque
```http
PATCH /api/produtos/{id}/estoque?quantidade=20
```

### Banco de Dados

- **PostgreSQL Master** (porta 5435)
- Operações de escrita
- Replicação assíncrona para replica

### Eventos RabbitMQ

Exchange: `produto.exchange`

Eventos publicados:
- `produto.created` → Queue: `produto.created.queue`
- `produto.updated` → Queue: `produto.updated.queue`
- `produto.deleted` → Queue: `produto.deleted.queue`

---

## 📖 Read Service (Porta 8082)

### Funcionalidades

- ✅ Buscar produto por ID
- ✅ Listar produtos (paginado)
- ✅ Buscar por categoria
- ✅ Buscar por termo
- ✅ Buscar por SKU
- ✅ Cache Redis
- ✅ Invalidação automática de cache via eventos
- ✅ Circuit Breaker
- ✅ Rate Limiting

### Endpoints

#### Buscar por ID
```http
GET /api/produtos/{id}
```

#### Listar Produtos (Paginado)
```http
GET /api/produtos?page=0&size=20&sortBy=nome&direction=ASC
```

#### Listar Todos
```http
GET /api/produtos/todos
```

#### Buscar por Categoria
```http
GET /api/produtos/categoria/Eletrônicos?page=0&size=20
```

#### Buscar por Termo
```http
GET /api/produtos/buscar?termo=notebook&page=0&size=20
```

#### Buscar por SKU
```http
GET /api/produtos/sku/DELL-INSP-15
```

### Cache Redis

**Estratégia de Cache:**
- Cache individual por produto (key: `produto:{id}`)
- Cache de listagem completa (`produtos-all`)
- Cache por categoria (`produtos-categoria:{categoria}`)
- Cache por SKU (`produto-sku:{sku}`)
- TTL: 1 hora

**Invalidação:**
- Automática via eventos RabbitMQ
- Quando um produto é criado/atualizado/deletado
- Cache específico + listagens são invalidados

### Banco de Dados

- **PostgreSQL Replica** (porta 5437)
- Operações de leitura
- Read-only connection

---

## 🔧 Configuração

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker (para PostgreSQL, Redis, RabbitMQ)

### Subir Infraestrutura

```bash
# Na raiz do projeto
docker-compose up -d postgres-produtos-master \
                   postgres-produtos-replica \
                   redis \
                   rabbitmq
```

### Executar Write Service

```bash
cd produtos-write-service
mvn clean package
mvn spring-boot:run
```

### Executar Read Service

```bash
cd produtos-read-service
mvn clean package
mvn spring-boot:run
```

## 🐳 Docker

### Build

```bash
# Write Service
cd produtos-write-service
mvn clean package
docker build -t produtos-write-service:latest .

# Read Service
cd produtos-read-service
mvn clean package
docker build -t produtos-read-service:latest .
```

## 📊 Observabilidade

### Métricas Prometheus

**Write Service:**
```
http://localhost:8081/actuator/prometheus
```

**Read Service:**
```
http://localhost:8082/actuator/prometheus
```

### Métricas Customizadas

**Write Service:**
- `produtos.criar` - Tempo de criação
- `produtos.atualizar` - Tempo de atualização
- `produtos.deletar` - Tempo de deleção
- `produtos.atualizar-estoque` - Tempo de atualização de estoque

**Read Service:**
- `produtos.buscar-id` - Tempo de busca por ID
- `produtos.listar` - Tempo de listagem
- `produtos.buscar-categoria` - Tempo de busca por categoria
- `produtos.buscar-termo` - Tempo de busca por termo
- `produtos.buscar-sku` - Tempo de busca por SKU

### Health Checks

```http
GET http://localhost:8081/actuator/health  # Write
GET http://localhost:8082/actuator/health  # Read
```

### Circuit Breakers

```http
GET http://localhost:8081/actuator/circuitbreakers
GET http://localhost:8082/actuator/circuitbreakers
```

## 🛡️ Resiliência

### Circuit Breaker

```yaml
Configuração:
- Sliding window: 10 requisições
- Failure rate threshold: 50%
- Wait duration: 10s
- Half-open state: 3 calls
```

### Rate Limiter

```yaml
Configuração:
- Limit: 100 requisições/segundo
- Timeout: 0s (fail fast)
```

### Retry

```yaml
Configuração:
- Max attempts: 3
- Wait duration: 1s
- Exponential backoff: 2x
```

## 🧪 Testando o Fluxo CQRS

### 1. Criar um produto (Write)

```bash
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teclado Mecânico",
    "descricao": "Teclado mecânico RGB",
    "preco": 450.00,
    "estoque": 25,
    "categoria": "Periféricos",
    "sku": "TEC-MEC-001"
  }'
```

### 2. Aguardar propagação (RabbitMQ → Event Consumer)

Evento será consumido e cache invalidado automaticamente.

### 3. Buscar produto (Read)

```bash
# Primeira busca: cache miss → busca no banco → armazena em cache
curl http://localhost:8082/api/produtos/1

# Segunda busca: cache hit → muito mais rápido
curl http://localhost:8082/api/produtos/1
```

### 4. Atualizar produto (Write)

```bash
curl -X PUT http://localhost:8081/api/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teclado Mecânico Premium",
    "descricao": "Teclado mecânico RGB Premium",
    "preco": 550.00,
    "estoque": 30,
    "categoria": "Periféricos",
    "sku": "TEC-MEC-001"
  }'
```

### 5. Cache invalidado automaticamente

Próxima busca no Read Service buscará dados atualizados.

## 📚 Vantagens do CQRS

✅ **Escalabilidade independente** - Read e Write podem escalar separadamente  
✅ **Performance de leitura** - Cache Redis para consultas rápidas  
✅ **Otimização de queries** - Read otimizado para consultas específicas  
✅ **Resiliência** - Falha no Write não afeta Read  
✅ **Eventual Consistency** - Aceitável para a maioria dos casos  

## ⚠️ Considerações

- **Eventual Consistency**: Há um pequeno delay entre escrita e leitura
- **Sincronização**: Garantir que eventos sejam processados
- **Monitoramento**: Acompanhar lag de replicação do PostgreSQL
- **Cache**: Estratégia de invalidação é crítica

## 🐛 Troubleshooting

### Problema: Cache não invalida

**Solução**: Verifique se RabbitMQ está rodando e consumidor está conectado:
```bash
docker-compose logs rabbitmq
```

### Problema: Read Service retorna dados desatualizados

**Solução**: Verifique replicação PostgreSQL:
```bash
docker exec -it postgres-produtos-replica psql -U admin -d produtos -c "SELECT pg_is_in_recovery();"
```

### Problema: Eventos não são consumidos

**Solução**: Verifique filas no RabbitMQ:
```
http://localhost:15672 (guest/guest)
```

## 📄 Licença

Este projeto está sob a licença MIT.
