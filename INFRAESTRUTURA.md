# 🏗️ Infraestrutura e Acesso aos Serviços

Guia completo para acessar todos os bancos de dados, caches, message brokers e ferramentas de observabilidade do sistema venda-distribuida.

---

## 📋 Sumário de Serviços

| Serviço | Porta | URL | Credenciais |
|---------|-------|-----|-------------|
| **PostgreSQL - Usuários** | 5434 | localhost:5434 | admin / admin123 |
| **PostgreSQL - Produtos (Master)** | 5435 | localhost:5435 | admin / admin123 |
| **PostgreSQL - Produtos (Replica)** | 5437 | localhost:5437 | admin / admin123 |
| **PostgreSQL - Vendas** | 5436 | localhost:5436 | admin / admin123 |
| **Redis** | 6379 | localhost:6379 | (sem auth) |
| **RabbitMQ Management** | 15672 | http://localhost:15672 | guest / guest |
| **RabbitMQ AMQP** | 5672 | localhost:5672 | - |
| **Kafka** | 9092 | localhost:9092 | - |
| **Zookeeper** | 2181 | localhost:2181 | - |
| **Prometheus** | 9090 | http://localhost:9090 | - |
| **Grafana** | 3000 | http://localhost:3000 | admin / admin |
| **Jaeger UI** | 16686 | http://localhost:16686 | - |
| **Loki** | 3100 | http://localhost:3100 | - |
| **Redis Commander** | 8081 | http://localhost:8081 | - |

---

## 🗄️ BANCOS DE DADOS POSTGRESQL

### 1. PostgreSQL - Usuários (Port 5434)

**Informações:**
- Database: `usuarios`
- User: `admin`
- Password: `admin123`
- Host: `localhost`
- Port: `5434`

**Acessar via psql:**
```bash
# Via terminal local
psql -h localhost -p 5434 -U admin -d usuarios

# Via container
docker exec -it postgres-usuarios psql -U admin -d usuarios
```

**Acessar via DBeaver/TablePlus:**
1. Criar nova conexão
2. Host: `localhost`, Port: `5434`
3. Database: `usuarios`
4. Username: `admin`, Password: `admin123`

**Tabelas principais:**
```sql
-- Ver todas as tabelas
\dt

-- Listar usuários
SELECT * FROM usuarios;

-- Ver schema
\d usuarios
```

**Estrutura:**
```sql
CREATE TABLE usuarios (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  senha VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  ativo BOOLEAN DEFAULT true,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_role ON usuarios(role);
```

---

### 2. PostgreSQL - Produtos Master (Port 5435)

**Informações:**
- Database: `produtos`
- User: `admin`
- Password: `admin123`
- Host: `localhost`
- Port: `5435`
- Role: **MASTER** (para escrita)

**Acessar:**
```bash
psql -h localhost -p 5435 -U admin -d produtos
```

**Operações:**
```sql
-- Listar produtos
SELECT * FROM produtos;

-- Buscar por SKU
SELECT * FROM produtos WHERE sku = 'NOTEBOOK-DELL-001';

-- Ver estoque
SELECT id, nome, estoque FROM produtos WHERE estoque < 10;

-- Atualizar estoque
UPDATE produtos SET estoque = estoque - 1 WHERE id = 1;
```

**Tabelas:**
```sql
CREATE TABLE produtos (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  preco DECIMAL(10, 2) NOT NULL,
  estoque INTEGER NOT NULL DEFAULT 0,
  sku VARCHAR(100) UNIQUE NOT NULL,
  categoria VARCHAR(100),
  ativo BOOLEAN DEFAULT true,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_produtos_sku ON produtos(sku);
CREATE INDEX idx_produtos_categoria ON produtos(categoria);
CREATE INDEX idx_produtos_estoque ON produtos(estoque);
```

---

### 3. PostgreSQL - Produtos Replica (Port 5437)

**Informações:**
- Database: `produtos`
- User: `admin`
- Password: `admin123`
- Host: `localhost`
- Port: `5437`
- Role: **REPLICA** (apenas leitura)

**Acessar:**
```bash
psql -h localhost -p 5437 -U admin -d produtos
```

**Importante:**
- ⚠️ **Apenas LEITURA** - Não é possível INSERT/UPDATE/DELETE
- Dados são replicados automaticamente do Master
- Usado pela `produtos-read-service` para melhor performance

```bash
# Tentar escrever vai gerar erro:
UPDATE produtos SET estoque = 100 WHERE id = 1;
# ERROR: cannot execute UPDATE in a read-only transaction
```

---

### 4. PostgreSQL - Vendas (Port 5436)

**Informações:**
- Database: `vendas`
- User: `admin`
- Password: `admin123`
- Host: `localhost`
- Port: `5436`

**Acessar:**
```bash
psql -h localhost -p 5436 -U admin -d vendas
```

**Tabelas:**
```sql
CREATE TABLE vendas (
  id BIGSERIAL PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  total DECIMAL(10, 2) NOT NULL,
  desconto DECIMAL(10, 2) DEFAULT 0,
  criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  processado_em TIMESTAMP,
  cancelado_em TIMESTAMP
);

CREATE TABLE venda_items (
  id BIGSERIAL PRIMARY KEY,
  venda_id BIGINT NOT NULL REFERENCES vendas(id),
  produto_id BIGINT NOT NULL,
  quantidade INTEGER NOT NULL,
  preco_unitario DECIMAL(10, 2) NOT NULL
);

CREATE INDEX idx_vendas_usuario ON vendas(usuario_id);
CREATE INDEX idx_vendas_status ON vendas(status);
```

**Consultas úteis:**
```sql
-- Vendas pendentes
SELECT * FROM vendas WHERE status = 'PENDENTE';

-- Total vendido por usuário
SELECT usuario_id, COUNT(*) as qtd, SUM(total) as total 
FROM vendas 
WHERE status = 'PROCESSADA'
GROUP BY usuario_id
ORDER BY total DESC;

-- Produto mais vendido
SELECT p.id, p.nome, SUM(vi.quantidade) as qtd_vendida
FROM venda_items vi
JOIN vendas v ON v.id = vi.venda_id
GROUP BY p.id, p.nome
ORDER BY qtd_vendida DESC
LIMIT 10;
```

---

## 🗂️ Replicação PostgreSQL

### Como funciona:

```
┌─────────────────────┐
│  Master (5435)      │
│  - Recebe writes    │
│  - Replica dados    │
└──────────┬──────────┘
           │ WAL Stream
           │ (replication)
           ▼
┌─────────────────────┐
│  Replica (5437)     │
│  - Apenas reads     │
│  - Cópia do master  │
└─────────────────────┘
```

**Verificar status da replicação:**
```bash
# No master
docker exec postgres-produtos-master psql -U admin -d produtos -c \
  "SELECT slot_name, restart_lsn FROM pg_replication_slots;"

# Ver WAL
docker exec postgres-produtos-master psql -U admin -d produtos -c \
  "SELECT pg_current_wal_lsn();"
```

---

## 💾 REDIS (Port 6379)

**Informações:**
- Host: `localhost`
- Port: `6379`
- Password: (vazio)
- Uso: Cache de produtos, sessões, tokens

### Acessar via redis-cli:

```bash
# Via container
docker exec -it redis redis-cli

# Via CLI local (se instalado)
redis-cli -h localhost -p 6379
```

### Comandos úteis:

```redis
# Ver chaves
KEYS *
KEYS produto:*

# Ver tipo de chave
TYPE chave

# Ver conteúdo
GET chave
HGETALL chave_hash
LRANGE chave_list 0 -1

# Informações do servidor
INFO

# Listar todas as keys com tipos
SCAN 0 MATCH * COUNT 100

# Limpear cache (⚠️ cuidado!)
FLUSHDB    # Limpa BD atual
FLUSHALL   # Limpa tudo

# TTL (Time To Live)
TTL chave  # Ver segundos até expirar

# Setando dados
SET chave "valor" EX 3600  # 1 hora
HSET hash campo valor
LPUSH lista item
```

### Acessar via Redis Commander (UI):

```
URL: http://localhost:8081
```

**Interface visual para:**
- Ver todas as keys
- Editar valores
- Monitorar em tempo real
- Deletar keys

---

## 🐰 RABBITMQ (Port 5672 & 15672)

**Informações:**
- Host: `localhost`
- AMQP Port: `5672`
- Management UI: `http://localhost:15672`
- Username: `guest`
- Password: `guest`

### Acessar Management UI:

```
URL: http://localhost:15672
Username: guest
Password: guest
```

**Filas principais:**
- `produto.updated` - Eventos de atualização de produtos
- `cache.invalidate` - Comandos para limpar cache

### Monitorar via CLI:

```bash
# Ver status do RabbitMQ
docker exec rabbitmq rabbitmqctl status

# Listar filas
docker exec rabbitmq rabbitmqctl list_queues name messages consumers

# Listar exchanges
docker exec rabbitmq rabbitmqctl list_exchanges

# Purgar fila (limpar mensagens)
docker exec rabbitmq rabbitmqctl purge_queue produto.updated
```

### Enviar mensagem de teste:

```bash
docker exec -it rabbitmq rabbitmq-diagnostics ping
```

---

## 📨 KAFKA (Port 9092) & ZOOKEEPER (Port 2181)

**Informações:**
- Kafka Host: `localhost:9092`
- Zookeeper: `localhost:2181`
- Uso: Event streaming para vendas, processamento assíncrono

### Tópicos principais:

```
- venda.created      → Nova venda criada
- venda.processed    → Venda processada com sucesso
- venda.failed       → Venda falhou
```

### Acessar via CLI:

```bash
# Listar tópicos
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Ver conteúdo de tópico (últimas mensagens)
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic venda.created \
  --from-beginning \
  --max-messages 10

# Enviar mensagem de teste
docker exec kafka bash -c 'echo "{\"vendaId\": 1}" | kafka-console-producer --broker-list localhost:9092 --topic venda.created'

# Ver estatísticas
docker exec kafka kafka-topics --describe --bootstrap-server localhost:9092 --topic venda.created
```

---

## 📊 OBSERVABILIDADE

### Prometheus (Port 9090)

```
URL: http://localhost:9090
```

**Acessar métricas:**
```
http://localhost:9090/metrics
http://localhost:9090/graph
```

**PromQL - Exemplos de queries:**

```promql
# Taxa de requisições por segundo
rate(http_requests_total[1m])

# Latência p95
histogram_quantile(0.95, http_request_duration_seconds_bucket)

# Taxa de erros
rate(http_requests_total{status=~"5.."}[1m])

# Saúde de cada serviço
up{job="usuarios-service"}
up{job="produtos-write-service"}
up{job="produtos-read-service"}
up{job="vendas-service"}
```

---

### Grafana (Port 3000)

```
URL: http://localhost:3000
Username: admin
Password: admin
```

**Dashboards disponíveis:**
1. **System Overview** - CPU, Memória, Disco
2. **Application Metrics** - Requisições, Erros, Latência
3. **Database Performance** - Queries lentas, Conexões
4. **Kafka** - Tópicos, Consumers, Lag
5. **RabbitMQ** - Filas, Exchanges, Taxa de mensagens

**Criar novo dashboard:**
1. Clique em `+` → `Dashboard`
2. Add Panel → Selecione `Prometheus`
3. Escreva PromQL
4. Save

---

### Jaeger (Port 16686)

```
URL: http://localhost:16686
```

**Rastreamento distribuído:**
- Ver fluxo completo de uma requisição
- Latência entre serviços
- Identificar gargalos
- Analyze errors

**Buscar traces:**
1. Selecione serviço (ex: `usuarios-service`)
2. Operação (ex: `login`)
3. Clique em `Find Traces`

---

### Loki (Port 3100)

```
URL: http://localhost:3100
```

**Agregação de logs:**

```bash
# Via curl
curl "http://localhost:3100/loki/api/v1/query?query={job='usuarios-service'}"

# Via Grafana
# Adicione Loki como datasource, depois crie dashboards com LogQL
```

**LogQL - Exemplos:**

```logql
# Logs de erro
{job="usuarios-service"} | level="ERROR"

# Logs do último minuto
{job="vendas-service"} | __timestamp__ > now() - 1m

# Contar erros
sum(count_over_time({job="produtos-write-service"} | level="ERROR" [5m]))
```

---

## 🚀 SCRIPTS DE ACESSO RÁPIDO

### Script: Conectar a um banco

```bash
#!/bin/bash
# script: connect-db.sh

case $1 in
  usuarios)
    psql -h localhost -p 5434 -U admin -d usuarios
    ;;
  produtos-master)
    psql -h localhost -p 5435 -U admin -d produtos
    ;;
  produtos-replica)
    psql -h localhost -p 5437 -U admin -d produtos
    ;;
  vendas)
    psql -h localhost -p 5436 -U admin -d vendas
    ;;
  redis)
    docker exec -it redis redis-cli
    ;;
  *)
    echo "Uso: ./connect-db.sh [usuarios|produtos-master|produtos-replica|vendas|redis]"
    exit 1
    ;;
esac
```

**Usar:**
```bash
chmod +x connect-db.sh
./connect-db.sh usuarios
./connect-db.sh redis
```

### Script: Monitorar Kafka

```bash
#!/bin/bash
# script: kafka-monitor.sh

echo "📨 Tópicos Kafka:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

echo -e "\n📊 Detalhes:"
docker exec kafka kafka-topics --describe --bootstrap-server localhost:9092

echo -e "\n⏱️  Consumindo últimas 5 mensagens de venda.created:"
docker exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic venda.created \
  --from-beginning \
  --max-messages 5
```

---

## 📈 CHECKLIST DE SAÚDE

Execute periodicamente para verificar o sistema:

```bash
#!/bin/bash
# script: health-check.sh

echo "🏥 HEALTH CHECK DO SISTEMA"
echo "=========================="

# PostgreSQL
echo -e "\n✅ PostgreSQL:"
for port in 5434 5435 5436 5437; do
  if pg_isready -h localhost -p $port 2>/dev/null; then
    echo "  ✓ Port $port OK"
  else
    echo "  ✗ Port $port ERRO"
  fi
done

# Redis
echo -e "\n✅ Redis:"
if redis-cli -h localhost -p 6379 ping &>/dev/null; then
  echo "  ✓ Redis OK"
else
  echo "  ✗ Redis ERRO"
fi

# RabbitMQ
echo -e "\n✅ RabbitMQ:"
if curl -s http://localhost:15672/api/overview -u guest:guest > /dev/null; then
  echo "  ✓ RabbitMQ OK"
else
  echo "  ✗ RabbitMQ ERRO"
fi

# Kafka
echo -e "\n✅ Kafka:"
if docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 &>/dev/null; then
  echo "  ✓ Kafka OK"
else
  echo "  ✗ Kafka ERRO"
fi

# Serviços da aplicação
echo -e "\n✅ Aplicação:"
for port in 8080 8081 8082 8083; do
  if curl -s http://localhost:$port/actuator/health | grep -q "UP"; then
    echo "  ✓ Port $port OK"
  else
    echo "  ✗ Port $port ERRO"
  fi
done

echo -e "\n=========================="
```

---

## 🔐 VARIÁVEIS DE AMBIENTE

Arquivo `.env`:

```bash
# PostgreSQL
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123
POSTGRES_USUARIOS_PORT=5434
POSTGRES_PRODUTOS_MASTER_PORT=5435
POSTGRES_PRODUTOS_REPLICA_PORT=5437
POSTGRES_VENDAS_PORT=5436

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Prometheus
PROMETHEUS_PORT=9090

# Grafana
GRAFANA_PORT=3000
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=admin

# Jaeger
JAEGER_PORT=16686

# Loki
LOKI_PORT=3100
```

---

## 🆘 TROUBLESHOOTING

### Porta já em uso

```bash
# Encontrar processo usando a porta
lsof -i :5434

# Matar processo
kill -9 <PID>
```

### Container não inicia

```bash
# Ver logs
docker logs postgres-usuarios
docker logs redis
docker logs rabbitmq

# Rebuild
docker-compose down -v
docker-compose up -d
```

### Banco de dados não conecta

```bash
# Verificar se está rodando
docker ps | grep postgres

# Verificar IP do container
docker inspect postgres-usuarios | grep IPAddress

# Testar conectividade
docker exec postgres-usuarios pg_isready -U admin -d usuarios
```

### Redis keys muito grande

```bash
# Limpar cache antigo
docker exec redis redis-cli --scan --pattern "produto:*" | xargs docker exec redis redis-cli del

# Monitorar uso de memória
docker exec redis redis-cli INFO memory
```

---

## 📞 RESUMO RÁPIDO

```bash
# Iniciar tudo
docker-compose up -d

# Ver status
docker-compose ps

# Parar tudo
docker-compose down

# Reiniciar infraestrutura
docker-compose restart

# Ver logs
docker-compose logs -f postgres-usuarios
docker-compose logs -f redis

# Conectar a um banco
psql -h localhost -p 5434 -U admin -d usuarios

# Acessar RabbitMQ
open http://localhost:15672

# Acessar Grafana
open http://localhost:3000
```

---

## 📚 DOCUMENTOS RELACIONADOS

- [README.md](README.md) - Visão geral do projeto
- [USUARIOS-CURL.md](USUARIOS-CURL.md) - Exemplos de API de usuários
- [PRODUTOS-CURL.md](PRODUTOS-CURL.md) - Exemplos de API de produtos
- [VENDAS-CURL.md](VENDAS-CURL.md) - Exemplos de API de vendas

