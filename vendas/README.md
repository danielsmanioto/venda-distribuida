# Venda Service

Microserviço responsável pela gestão de vendas no sistema de venda distribuída. Integra-se com o serviço de produtos para validação e publica eventos via Kafka.

## 🏗️ Arquitetura

### Dependências
- **produtos-read-service**: Validação de produtos e preços
- **Kafka**: Publicação de eventos de venda
- **PostgreSQL**: Armazenamento de vendas

### Eventos Kafka Publicados
- `venda.criada`: Quando uma nova venda é criada
- `venda.processada`: Quando uma venda é processada
- `venda.cancelada`: Quando uma venda é cancelada

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- PostgreSQL 15
- Kafka
- Resilience4j (Circuit Breaker, Rate Limiter)
- WebClient (comunicação HTTP)
- Micrometer + Prometheus + Jaeger

## 📦 Endpoints

### Criar Venda
```bash
POST /api/vendas
Content-Type: application/json

{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ]
}
```

### Buscar Venda por ID
```bash
GET /api/vendas/{id}
```

### Buscar Vendas por Usuário
```bash
GET /api/vendas/usuario/{usuarioId}
```

### Listar Vendas (Paginado)
```bash
GET /api/vendas?page=0&size=20&sortBy=id&direction=DESC
```

### Cancelar Venda
```bash
PUT /api/vendas/{id}/cancelar
```

## 🔧 Configuração

### Variáveis de Ambiente
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5436/vendas
spring.datasource.username=vendas_user
spring.datasource.password=vendas_pass

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Produtos Service
produtos.service.url=http://localhost:8082

# Resilience4j
resilience4j.circuitbreaker.instances.vendas.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.vendas.wait-duration-in-open-state=60000
resilience4j.ratelimiter.instances.vendas.limit-for-period=100
```

## 🐳 Docker

### Build
```bash
mvn clean package -DskipTests
docker build -t venda-service:latest .
```

### Run
```bash
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-vendas:5432/vendas \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e PRODUTOS_SERVICE_URL=http://produtos-read-service:8082 \
  venda-service:latest
```

## 📊 Observabilidade

### Actuator Endpoints
- Health: http://localhost:8083/actuator/health
- Metrics: http://localhost:8083/actuator/metrics
- Prometheus: http://localhost:8083/actuator/prometheus

### Métricas Customizadas
- `vendas.criar`: Tempo para criar venda
- `vendas.buscar`: Tempo para buscar venda
- `vendas.cancelar`: Tempo para cancelar venda

### Rastreamento Distribuído
Jaeger UI: http://localhost:16686

## 🔄 Fluxo de Negócio

1. **Criar Venda**
   - Valida produtos via produtos-read-service
   - Verifica estoque disponível
   - Calcula valor total
   - Persiste venda com status PENDENTE
   - Publica evento venda.criada no Kafka

2. **Cancelar Venda**
   - Valida status da venda
   - Atualiza status para CANCELADA
   - Publica evento venda.cancelada no Kafka

## 🛡️ Resilience

### Circuit Breaker
- Taxa de falha: 50%
- Tempo em estado aberto: 60s
- Fallback: Mensagem de serviço indisponível

### Rate Limiter
- Limite: 100 requisições por segundo

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify -P integration-tests
```

## 📝 Logs

Logs estruturados em JSON com informações de rastreamento:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "level": "INFO",
  "service": "venda-service",
  "trace": "abc123",
  "span": "def456",
  "message": "Venda criada com sucesso: ID 1"
}
```

## 🔐 Segurança

- Validação de entrada com Bean Validation
- Tratamento de exceções global
- Logs de auditoria

## 📋 Status da Venda

- `PENDENTE`: Venda criada, aguardando processamento
- `CONFIRMADA`: Venda confirmada e processada
- `CANCELADA`: Venda cancelada

## 🚦 Health Check

```bash
curl http://localhost:8083/actuator/health
```

Resposta:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "kafka": {
      "status": "UP"
    }
  }
}
```
