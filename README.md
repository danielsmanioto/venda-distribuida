# 🛒 Sistema de Venda Online

Sistema de e-commerce distribuído com arquitetura de microserviços, implementando padrões modernos de escalabilidade, resiliência e observabilidade.

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Requisitos do Sistema](#-requisitos-do-sistema)
- [Microserviços](#-microserviços)
- [Infraestrutura](#-infraestrutura)
- [Padrões Arquiteturais](#-padrões-arquiteturais)
- [Observabilidade](#-observabilidade)
- [Como Executar](#-como-executar)
- [Fluxos Principais](#-fluxos-principais)

---

## 🎯 Visão Geral

Sistema de e-commerce desenvolvido com arquitetura de microserviços, preparado para atender alta demanda com requisitos rigorosos de performance e disponibilidade.

### Capacidade do Sistema

- **Vendas**: 1.000 vendas/dia
- **Tráfego**: 30.000 visitas/dia
- **RPS Normal**: 1 requisição/segundo
- **RPS Pior Cenário**: 100 requisições/segundo
- **Produtos**: 700 produtos cadastrados

---

## 🏗️ Arquitetura

O sistema utiliza uma arquitetura de microserviços com os seguintes componentes principais:

```
┌──────────┐
│ Usuário  │
└────┬─────┘
     │
┌────▼────────────┐
│   MFE Front     │
└────┬────────┬───┘
     │        │
┌────▼────┐   └───────────────┐
│   DNS   │                   │
│   CDN   │                   │
└─────────┘                   │
                              │
                    ┌─────────▼─────────────────────────────────┐
                    │         API Gateway                        │
                    │  (Rate Limiting + Circuit Breaker)         │
                    └─┬──────┬────────┬────────────┬────────────┘
                      │      │        │            │
           ┌──────────┘      │        └────────┐   └────────┐
           │                 │                 │            │
    ┌──────▼──────┐   ┌─────▼──────┐   ┌─────▼──────┐   ┌─▼─────────┐
    │  Usuários   │   │  Produtos  │   │  Produtos  │   │   Venda   │
    │  Service    │   │   Write    │   │    Read    │   │  Service  │
    │   :8080     │   │  Service   │   │  Service   │   │   :8082   │
    └──────┬──────┘   │   :8081    │   │   :8081    │   └─────┬─────┘
           │          └─────┬──────┘   └─────┬──────┘         │
           │                │                 │                │
    ┌──────▼──────┐   ┌────▼──────┐    ┌────▼──────┐   ┌─────▼──────┐
    │ PostgreSQL  │   │PostgreSQL │    │   Redis   │   │   Kafka    │
    │  usuarios   │   │  Master   │    │  Cache    │   └─────┬──────┘
    │   :5434     │   │   :5435   │    │  :6379    │         │
    └─────────────┘   └─────┬─────┘    └───────────┘         │
                            │                                 │
                      ┌─────▼──────┐                    ┷─────▼──────┐
                      │PostgreSQL  │                    │  Processa  │
                      │  Replica   │                    │   Venda    │
                      │   :5437    │                    │  Service   │
                      └────────────┘                    │   :8082    │
                                                        └─────┬──────┘
                                                              │
                                                       ┌──────▼──────┐
                                                       │ PostgreSQL  │
                                                       │   :5436     │
                                                       └─────────────┘
```

---

## 📊 Requisitos do Sistema

### Requisitos Funcionais

- ✅ Login com autenticação via JWT Token
- ✅ Cadastro e gerenciamento de produtos
- ✅ Busca de produtos para venda no site
- ✅ Processamento de vendas

### Requisitos Não Funcionais

| Requisito | Descrição |
|-----------|-----------|
| **Circuit Breaker** | Implementado em todos os serviços para evitar cascata de falhas |
| **Cache Strategy** | Sistema de cache com Redis e estratégia de invalidação |
| **Rate Limiting** | Controle de taxa no API Gateway |
| **CQRS** | Separação de leitura/escrita no serviço de produtos |
| **Event-Driven** | Processamento assíncrono de vendas via Kafka |

---

## 🔧 Microserviços

### 1. **usuarios-service** (:8080)
Gerenciamento de usuários e autenticação.

- **Responsabilidades**:
  - Cadastro e autenticação de usuários
  - Geração e validação de tokens JWT
  - Gerenciamento de perfis
- **Banco de Dados**: PostgreSQL (:5434)
- **Padrões**: Circuit Breaker

### 2. **produtos-write-service** (:8081)
Serviço responsável pela escrita de produtos (CQRS - Command).

- **Responsabilidades**:
  - Criação de produtos
  - Atualização de produtos
  - Deleção de produtos
  - Publicação de eventos de mudança
- **Banco de Dados**: PostgreSQL Master (:5435)
- **Message Queue**: RabbitMQ (para invalidação de cache)
- **Padrões**: Circuit Breaker, Event Sourcing

### 3. **produtos-read-service** (:8081)
Serviço responsável pela leitura de produtos (CQRS - Query).

- **Responsabilidades**:
  - Busca de produtos
  - Listagem de catálogo
  - Cache de consultas frequentes
- **Cache**: Redis (:6379)
- **Banco de Dados**: PostgreSQL Replica (:5437)
- **Padrões**: Circuit Breaker, Cache-Aside Pattern

### 4. **venda-service** (:8082)
Serviço de registro de vendas.

- **Responsabilidades**:
  - Recepção de pedidos
  - Validação de estoque
  - Publicação de eventos de venda
- **Message Broker**: Kafka
- **Padrões**: Circuit Breaker, Event-Driven

### 5. **processa-venda-service** (:8082)
Serviço de processamento assíncrono de vendas.

- **Responsabilidades**:
  - Consumo de eventos do Kafka
  - Processamento de pagamentos
  - Atualização de estoque
  - Persistência de vendas
- **Message Broker**: Kafka (Consumer)
- **Banco de Dados**: PostgreSQL (:5436)
- **Padrões**: Event-Driven, Saga Pattern

### 6. **notificacao-service** (:8085)
Serviço de notificações aos clientes.

- **Responsabilidades**:
  - Envio de e-mails de confirmação
  - Notificações de status de pedidos
  - Alertas de promoções
- **Message Broker**: Kafka (Consumer)
- **Padrões**: Event-Driven

### 7. **produtos-cache-service**
Serviço auxiliar para manutenção do cache.

- **Responsabilidades**:
  - Atualização programada do cache
  - Invalidação de cache por eventos
  - Sincronização Redis com PostgreSQL
- **Scheduler**: CRON Jobs
- **Message Queue**: RabbitMQ (Consumer)

---

## 🗄️ Infraestrutura

### Bancos de Dados

#### PostgreSQL
- **usuarios** (:5434) - Dados de usuários e autenticação
- **produtos-master** (:5435) - Base principal de produtos (Write)
- **produtos-replica** (:5437) - Réplica de leitura (Read)
- **vendas** (:5436) - Dados de vendas processadas

### Cache
- **Redis** (:6379) - Cache de produtos e consultas frequentes

### Message Brokers

#### Apache Kafka
- **Tópicos**:
  - `venda.created` - Novas vendas registradas
  - `venda.processed` - Vendas processadas com sucesso
  - `venda.failed` - Vendas que falharam

#### RabbitMQ
- **Filas**:
  - `produto.updated` - Eventos de atualização de produtos
  - `cache.invalidate` - Comandos de invalidação de cache

### Gateway & CDN
- **API Gateway** - Ponto único de entrada, rate limiting
- **DNS/CDN** - Distribuição de conteúdo estático e cache de frontend

---

## 🎨 Padrões Arquiteturais

### 1. **CQRS (Command Query Responsibility Segregation)**
Separação entre operações de leitura e escrita no domínio de produtos:
- **Write**: `produtos-write-service` → PostgreSQL Master
- **Read**: `produtos-read-service` → Redis Cache → PostgreSQL Replica

**Benefícios**:
- Otimização independente de leitura e escrita
- Escalabilidade horizontal do serviço de leitura
- Cache eficiente de consultas

### 2. **Event-Driven Architecture**
Comunicação assíncrona via eventos:
- Vendas processadas via Kafka
- Notificações desacopladas
- Invalidação de cache via RabbitMQ

**Benefícios**:
- Desacoplamento de serviços
- Resiliência a falhas
- Processamento assíncrono

### 3. **Circuit Breaker**
Implementado em todos os microserviços para evitar cascata de falhas.

**Estados**:
- **Closed**: Requisições fluem normalmente
- **Open**: Requisições são rejeitadas imediatamente
- **Half-Open**: Testa se o serviço voltou

**Configuração Sugerida**:
```yaml
circuitBreaker:
  failureThreshold: 50%
  slowCallThreshold: 5s
  waitDurationInOpenState: 30s
  slidingWindowSize: 100
```

### 4. **Cache Strategy**

#### Cache-Aside Pattern
```
1. Requisição → produtos-read-service
2. Verifica Redis
   ├─ Cache Hit → Retorna dados
   └─ Cache Miss → 
      ├─ Busca PostgreSQL Replica
      ├─ Armazena no Redis (TTL: 15min)
      └─ Retorna dados
```

#### Invalidação de Cache
```
1. Produto atualizado (produtos-write-service)
2. Publica evento no RabbitMQ
3. produtos-cache-service consome evento
4. Invalida cache específico no Redis
5. Próxima leitura irá buscar dados atualizados
```

### 5. **Rate Limiting**
Implementado no API Gateway para proteção contra sobrecarga.

**Configuração**:
```yaml
rateLimit:
  global: 100 req/s
  perUser: 10 req/s
  perIP: 50 req/s
```

---

## 📈 Observabilidade

Sistema completo de monitoramento e rastreamento distribuído.

### Stack de Observabilidade

```
┌─────────────────────────────────────────────────────┐
│            Observabilidade Stack                     │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │  Prometheus  │  │     Loki     │  │  Jaeger   │ │
│  │    :9090     │  │    :3100     │  │  :16686   │ │
│  │  (Métricas)  │  │    (Logs)    │  │ (Traces)  │ │
│  └──────┬───────┘  └──────┬───────┘  └─────┬─────┘ │
│         │                 │                 │       │
│         └─────────────────┼─────────────────┘       │
│                           │                         │
│                    ┌──────▼───────┐                 │
│                    │   Grafana    │                 │
│                    │     :3000    │                 │
│                    │ (Dashboards) │                 │
│                    └──────────────┘                 │
└─────────────────────────────────────────────────────┘
```

### 1. **Prometheus** (:9090)
Coleta e armazena métricas de todos os serviços.

**Métricas Coletadas**:
- Request rate (req/s)
- Error rate (%)
- Response time (p50, p95, p99)
- Circuit breaker status
- Cache hit/miss ratio
- Database connection pool
- JVM metrics (se Java)

### 2. **Grafana** (:3000)
Visualização unificada de métricas, logs e traces.

**Dashboards**:
- Overview geral do sistema
- Performance por serviço
- Status de saúde dos bancos de dados
- Monitoramento de filas (Kafka/RabbitMQ)
- Análise de cache
- Circuit breaker status

### 3. **Loki** (:3100)
Agregação e busca de logs centralizados.

**Log Levels**:
- ERROR: Erros que requerem atenção
- WARN: Situações anormais mas não críticas
- INFO: Eventos importantes do sistema
- DEBUG: Informações detalhadas para troubleshooting

### 4. **Jaeger** (:16686)
Rastreamento distribuído de requisições.

**Benefícios**:
- Visualização de latência entre serviços
- Identificação de gargalos
- Análise de dependências
- Debug de erros distribuídos

---

## 🌐 URLs de Acesso

Após inicializar o sistema, os seguintes serviços estarão disponíveis:

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Frontend** | http://localhost:3000 | Interface do usuário (MFE) |
| **API Gateway** | http://localhost/api | Ponto de entrada principal da API |
| **Swagger - Usuários** | http://localhost:8080/swagger-ui.html | Documentação API de usuários |
| **Swagger - Produtos** | http://localhost:8081/swagger-ui.html | Documentação API de produtos |
| **Swagger - Vendas** | http://localhost:8082/swagger-ui.html | Documentação API de vendas |
| **Prometheus** | http://localhost:9090 | Métricas do sistema |
| **Grafana** | http://localhost:3000 | Dashboards e visualizações |
| **Jaeger** | http://localhost:16686 | Tracing distribuído |
| **Loki** | http://localhost:3100 | Agregação de logs |
| **RabbitMQ Management** | http://localhost:15672 | Console de gerenciamento RabbitMQ |
| **Kafka UI** | http://localhost:8089 | Interface para visualizar tópicos Kafka |
| **Redis Commander** | http://localhost:8081 | Interface web para Redis |

### Credenciais Padrão

| Serviço | Usuário | Senha |
|---------|---------|-------|
| **Grafana** | admin | admin |
| **RabbitMQ** | guest | guest |
| **PostgreSQL** | postgres | postgres |

---

## 🚀 Como Executar

### Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+
- 8GB RAM disponível
- Portas disponíveis: 8080-8085, 5434-5437, 6379, 9090, 3000, 3100, 16686

### Inicialização Completa

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/venda-online.git
cd venda-online

# Inicie toda a infraestrutura
docker-compose up -d

# Aguarde todos os serviços ficarem saudáveis
docker-compose ps

# Inicialize os bancos de dados
docker-compose exec usuarios-db psql -U postgres -f /init/schema.sql
docker-compose exec produtos-db psql -U postgres -f /init/schema.sql
docker-compose exec vendas-db psql -U postgres -f /init/schema.sql

# Acesse o sistema
# Frontend: http://localhost:3000
# API Gateway: http://localhost/api
# Grafana: http://localhost:3000
# Prometheus: http://localhost:9090
# Jaeger: http://localhost:16686
```

### Execução por Componente

```bash
# Apenas bancos de dados
docker-compose up -d postgres-usuarios postgres-produtos-master postgres-produtos-replica postgres-vendas

# Apenas message brokers
docker-compose up -d kafka rabbitmq

# Apenas cache
docker-compose up -d redis

# Apenas observabilidade
docker-compose up -d prometheus grafana loki jaeger

# Apenas serviços de aplicação
docker-compose up -d usuarios-service produtos-write-service produtos-read-service venda-service
```

### Verificação de Saúde

```bash
# Health check de todos os serviços
curl http://localhost/health

# Health check individual
curl http://localhost:8080/actuator/health  # usuarios-service
curl http://localhost:8081/actuator/health  # produtos-service
curl http://localhost:8082/actuator/health  # venda-service
```

---

## 🔄 Fluxos Principais

### Fluxo 1: Consulta de Produto

```
┌────────┐       ┌─────────┐       ┌──────────────┐       ┌───────┐       ┌──────────┐
│ Client │──────>│   CDN   │──────>│ API Gateway  │──────>│ Redis │──────>│PostgreSQL│
└────────┘       └─────────┘       │(Rate Limit)  │       │(Cache)│       │ Replica  │
                                    └──────────────┘       └───────┘       └──────────┘
     1. Request         2. Static        3. Dynamic         4. Cache         5. DB Query
                       Content           Route              Lookup          (if miss)

Tempo total: ~50ms (cache hit) | ~200ms (cache miss)
```

**Passos Detalhados**:
1. Cliente acessa produto via frontend
2. CDN serve assets estáticos (JS, CSS, imagens)
3. API Gateway aplica rate limiting e circuit breaker
4. produtos-read-service consulta Redis
5. Se cache miss: busca PostgreSQL Replica
6. Armazena resultado no Redis (TTL: 15min)
7. Retorna dados ao cliente

### Fluxo 2: Cadastro/Atualização de Produto

```
┌────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────┐    ┌──────────┐
│ Admin  │───>│ API Gateway  │───>│produtos-write│───>│PostgreSQL│───>│ RabbitMQ │
└────────┘    └──────────────┘    │   service    │    │  Master  │    └────┬─────┘
                                   └──────────────┘    └──────────┘         │
                                                                             │
                                   ┌──────────────────────────────────────────┘
                                   ▼
                           ┌───────────────┐         ┌───────┐
                           │produtos-cache │────────>│ Redis │
                           │   service     │ Invalida└───────┘
                           └───────────────┘

Tempo total: ~300ms
```

**Passos Detalhados**:
1. Admin envia dados do produto
2. API Gateway valida JWT e permissões
3. produtos-write-service valida dados
4. Persiste no PostgreSQL Master
5. Publica evento `produto.updated` no RabbitMQ
6. produtos-cache-service consome evento
7. Invalida cache específico no Redis
8. Replicação automática para PostgreSQL Replica

### Fluxo 3: Processamento de Venda

```
┌────────┐    ┌──────────┐    ┌─────────────┐    ┌────────┐
│ Client │───>│   API    │───>│venda-service│───>│ Kafka  │
└────────┘    │ Gateway  │    └─────────────┘    └───┬────┘
              └──────────┘                            │
      1. Pedido    2. JWT         3. Validação        │ 4. Evento
                   Válido         Estoque             │    Async
                                                      │
              ┌───────────────────────────────────────┴──────────────┐
              ▼                                                      ▼
    ┌──────────────────┐                                  ┌──────────────────┐
    │processa-venda    │─────────────────────────────────>│   notificacao    │
    │    service       │          6. Notificar            │     service      │
    └────────┬─────────┘             Cliente              └──────────────────┘
             │                                                      │
             │ 5. Persiste                                          │ 7. Email
             ▼                                                      ▼
    ┌──────────────┐                                      ┌──────────────────┐
    │  PostgreSQL  │                                      │  SMTP/SendGrid   │
    │    Vendas    │                                      └──────────────────┘
    └──────────────┘

Tempo total: ~150ms (resposta imediata) + processamento assíncrono
```

**Passos Detalhados**:
1. Cliente finaliza compra no frontend
2. API Gateway valida token JWT
3. venda-service valida estoque e preços
4. Registra venda com status "PENDING"
5. Publica evento `venda.created` no Kafka
6. Retorna confirmação imediata ao cliente (202 Accepted)
7. **[Assíncrono]** processa-venda-service consome evento
8. **[Assíncrono]** Processa pagamento
9. **[Assíncrono]** Atualiza estoque
10. **[Assíncrono]** Persiste venda com status "COMPLETED"
11. **[Assíncrono]** Publica evento `venda.processed`
12. **[Assíncrono]** notificacao-service envia email de confirmação

### Fluxo 4: Circuit Breaker em Ação

```
Normal Flow:
┌────────┐     ┌─────────────┐     ┌──────────┐
│ Client │────>│   Service   │────>│ Database │
└────────┘     │  [CLOSED]   │     └──────────┘
               └─────────────┘
                    ✅ OK

Failure Detected:
┌────────┐     ┌─────────────┐     ┌──────────┐
│ Client │────>│   Service   │─ X─>│ Database │
└────────┘     │   [OPEN]    │     │ (DOWN)   │
               └─────┬───────┘     └──────────┘
                     │
                     └──> ⚠️ Fallback Response
                          (Cache ou erro rápido)

Recovery Test:
┌────────┐     ┌─────────────┐     ┌──────────┐
│ Client │────>│   Service   │────>│ Database │
└────────┘     │ [HALF-OPEN] │     └──────────┘
               └─────────────┘
                    ✅ OK → [CLOSED]
                    ❌ Fail → [OPEN]
```

---

## 📚 Documentação Adicional

### APIs

Toda documentação de APIs está disponível via Swagger:
- **usuarios-service**: http://localhost:8080/swagger-ui.html
- **produtos-service**: http://localhost:8081/swagger-ui.html
- **venda-service**: http://localhost:8082/swagger-ui.html

### Postman Collection

Importe a collection disponível em `/docs/postman_collection.json` para ter todos os endpoints prontos para teste.

### Diagramas

- **Arquitetura Completa**: `/docs/architecture.drawio`
- **Fluxo de Vendas**: `/docs/sales-flow.drawio`
- **Modelo de Dados**: `/docs/database-schema.drawio`

---

## 🛡️ Segurança

- ✅ Autenticação JWT em todos os endpoints protegidos
- ✅ Rate limiting no API Gateway
- ✅ HTTPS obrigatório em produção
- ✅ Secrets gerenciados via Vault/AWS Secrets Manager
- ✅ Sanitização de inputs
- ✅ SQL Injection prevention (Prepared Statements)
- ✅ CORS configurado adequadamente

---

## 🧪 Testes

```bash
# Testes unitários
./mvnw test

# Testes de integração
./mvnw verify

# Testes de carga (via K6)
k6 run tests/load/checkout-flow.js

# Testes de contrato (via Pact)
./mvnw pact:verify
```

---

## 📦 Deploy

### Ambientes

- **Development**: Docker Compose local
- **Staging**: Kubernetes (EKS/GKE/AKS)
- **Production**: Kubernetes com auto-scaling

### CI/CD Pipeline

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│   Git    │───>│  Build   │───>│  Tests   │───>│  Docker  │───>│  Deploy  │
│  Commit  │    │  (Maven) │    │ (JUnit)  │    │  Image   │    │   K8s    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
```

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Time

- **Tech Lead**: [Seu Nome]
- **Backend**: [Time Backend]
- **Frontend**: [Time Frontend]
- **DevOps**: [Time DevOps]

---

## 📞 Suporte

- **Documentação**: https://docs.vendaonline.com
- **Issues**: https://github.com/seu-usuario/venda-online/issues
- **Slack**: #venda-online-dev
- **Email**: dev@vendaonline.com

---

<div align="center">

**Feito com ❤️ pelo Time de Engenharia**

[🔝 Voltar ao topo](#-sistema-de-venda-online)

</div>
