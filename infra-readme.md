# 🚀 Infraestrutura E-commerce - Docker

Infraestrutura completa para o projeto de e-commerce com microserviços.

## 📋 O que está incluído

### Bancos de Dados
- **PostgreSQL Usuários** (porta 5434)
- **PostgreSQL Produtos Master** (porta 5435)
- **PostgreSQL Produtos Replica** (porta 5437) - Read-only replica
- **PostgreSQL Vendas** (porta 5436)

### Cache
- **Redis** (porta 6379) - Cache de produtos

### Mensageria
- **Kafka** (porta 9092) - Eventos assíncronos de vendas
- **Zookeeper** (porta 2181) - Gerenciamento do Kafka
- **Kafka UI** (porta 8089) - Interface web para gerenciar tópicos
- **RabbitMQ** (porta 5672 / Management UI: 15672) - Invalidação de cache

### Observabilidade
- **Prometheus** (porta 9090) - Coleta de métricas
- **Grafana** (porta 3000) - Visualização de métricas e logs
- **Loki** (porta 3100) - Agregação de logs
- **Promtail** - Coleta de logs dos serviços
- **Jaeger** (porta 16686) - Distributed tracing

### Exporters
- **PostgreSQL Exporter** (porta 9187) - Métricas do PostgreSQL
- **Redis Exporter** (porta 9121) - Métricas do Redis

## 🚀 Como usar

### 1. Subir toda a infraestrutura

```bash
docker-compose up -d
```

### 2. Verificar status dos containers

```bash
docker-compose ps
```

### 3. Ver logs de um serviço específico

```bash
docker-compose logs -f postgres-usuarios
docker-compose logs -f kafka
docker-compose logs -f prometheus
```

### 4. Parar todos os serviços

```bash
docker-compose down
```

### 5. Parar e remover volumes (CUIDADO: apaga todos os dados!)

```bash
docker-compose down -v
```

## 🔐 Credenciais Padrão

### PostgreSQL
- **Usuário:** admin
- **Senha:** admin123

### Redis
- **Senha:** redis123

### RabbitMQ
- **Usuário:** admin
- **Senha:** admin123
- **Management UI:** http://localhost:15672

### Grafana
- **Usuário:** admin
- **Senha:** admin123
- **URL:** http://localhost:3000

## 🌐 URLs de Acesso

| Serviço | URL | Descrição |
|---------|-----|-----------|
| Prometheus | http://localhost:9090 | Métricas |
| Grafana | http://localhost:3000 | Dashboards |
| Jaeger | http://localhost:16686 | Tracing |
| RabbitMQ | http://localhost:15672 | Management Console |
| Kafka UI | http://localhost:8089 | Kafka Topics |

## 🔧 Conexões para os Microserviços

### PostgreSQL

**Usuários:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/usuarios
    username: admin
    password: admin123
```

**Produtos (Master - Write):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5435/produtos
    username: admin
    password: admin123
```

**Produtos (Replica - Read):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5437/produtos
    username: admin
    password: admin123
```

**Vendas:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5436/vendas
    username: admin
    password: admin123
```

### Redis

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: redis123
```

### Kafka

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: ecommerce-group
```

### RabbitMQ

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
```

### Jaeger (Tracing)

```yaml
opentelemetry:
  exporter:
    jaeger:
      endpoint: http://localhost:14250
```

## 📊 Configuração do Grafana

Após subir o Grafana (http://localhost:3000):

1. Faça login com admin/admin123
2. Os datasources já estarão configurados automaticamente:
   - Prometheus
   - Loki
   - Jaeger

3. Importe dashboards prontos:
   - Spring Boot Dashboard: ID 6756
   - PostgreSQL Dashboard: ID 9628
   - Redis Dashboard: ID 11835
   - Kafka Dashboard: ID 7589

## 🧪 Testando a Infraestrutura

### Testar PostgreSQL

```bash
docker exec -it postgres-usuarios psql -U admin -d usuarios -c "SELECT version();"
```

### Testar Redis

```bash
docker exec -it redis redis-cli -a redis123 ping
```

### Testar RabbitMQ

```bash
docker exec -it rabbitmq rabbitmqctl status
```

### Testar Kafka

```bash
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

## 📁 Estrutura de Diretórios

```
.
├── docker-compose.yml
├── docker/
│   ├── prometheus/
│   │   └── prometheus.yml
│   ├── grafana/
│   │   ├── datasources/
│   │   │   └── datasources.yml
│   │   └── dashboards/
│   ├── loki/
│   │   └── loki-config.yml
│   ├── promtail/
│   │   └── promtail-config.yml
│   └── postgres/
│       └── init-replication.sh
└── logs/
    ├── usuarios-service/
    ├── produtos-write-service/
    ├── produtos-read-service/
    ├── venda-service/
    ├── processa-venda-service/
    ├── notificacao-service/
    └── produtos-cache-service/
```

## 🔍 Monitoramento

### Prometheus Queries Úteis

**Request Rate:**
```promql
rate(http_server_requests_seconds_count[5m])
```

**Request Latency (p95):**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

**Redis Hit Rate:**
```promql
redis_keyspace_hits_total / (redis_keyspace_hits_total + redis_keyspace_misses_total) * 100
```

**PostgreSQL Connections:**
```promql
pg_stat_database_numbackends
```

## 🐛 Troubleshooting

### Problema: Container não sobe

```bash
# Ver logs detalhados
docker-compose logs <nome-do-container>

# Verificar se a porta está em uso
netstat -tulpn | grep <porta>
```

### Problema: Kafka não conecta

```bash
# Verificar se Zookeeper está rodando
docker-compose ps zookeeper

# Recriar containers
docker-compose restart zookeeper kafka
```

### Problema: PostgreSQL replica não sincroniza

```bash
# Verificar status da replicação no master
docker exec -it postgres-produtos-master psql -U admin -d produtos -c "SELECT * FROM pg_stat_replication;"

# Ver logs da replica
docker-compose logs postgres-produtos-replica
```

## 📈 Performance Tuning

### PostgreSQL

Para ambientes de produção, ajuste no `docker-compose.yml`:

```yaml
command:
  - "postgres"
  - "-c"
  - "shared_buffers=256MB"
  - "-c"
  - "effective_cache_size=1GB"
  - "-c"
  - "max_connections=200"
```

### Redis

```yaml
command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
```

### Kafka

```yaml
environment:
  KAFKA_HEAP_OPTS: "-Xmx1G -Xms1G"
```

## 🎯 Próximos Passos

1. Criar os microserviços Spring Boot
2. Configurar conexões nos `application.yml`
3. Implementar endpoints `/actuator/prometheus`
4. Configurar logging estruturado (JSON)
5. Adicionar spans do Jaeger
6. Criar dashboards customizados no Grafana

## 📝 Notas Importantes

- **Volumes:** Todos os dados são persistidos em volumes Docker
- **Network:** Todos os serviços estão na mesma rede `ecommerce-network`
- **Health Checks:** Cada serviço tem health check configurado
- **Replicação PostgreSQL:** A replica é read-only e sincroniza automaticamente

## 🆘 Suporte

Em caso de dúvidas, consulte a documentação oficial:
- [PostgreSQL](https://www.postgresql.org/docs/)
- [Redis](https://redis.io/docs/)
- [Kafka](https://kafka.apache.org/documentation/)
- [RabbitMQ](https://www.rabbitmq.com/documentation.html)
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/)
- [Jaeger](https://www.jaegertracing.io/docs/)

---

**Desenvolvido para o projeto E-commerce Microserviços** 🚀
