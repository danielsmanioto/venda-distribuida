# 🚀 Guia de Execução - Venda Distribuída

Guia completo para executar todos os serviços do sistema de venda distribuída.

---

## 📦 Serviços Implementados

### ✅ Backend (Microserviços)

1. **usuarios-service** - Porta 8080
   - Autenticação JWT
   - Gerenciamento de usuários
   - PostgreSQL :5434

2. **produtos-write-service** - Porta 8081
   - CQRS - Write side
   - Criação e atualização de produtos
   - RabbitMQ para eventos
   - PostgreSQL Master :5435

3. **produtos-read-service** - Porta 8082
   - CQRS - Read side
   - Consultas otimizadas com cache
   - Redis cache
   - PostgreSQL Replica :5437

4. **vendas-service** - Porta 8083
   - Processamento de vendas
   - Integração com produtos
   - Kafka para eventos
   - PostgreSQL :5436

### ✅ Frontend

- **frontend** - Porta 5173
  - React + Vite
  - Login de usuários
  - Listagem de produtos
  - Interface simples e responsiva

### 🔧 Infraestrutura

- PostgreSQL (4 instâncias)
- Redis (cache)
- RabbitMQ (eventos de produtos)
- Kafka + Zookeeper (eventos de vendas)
- Prometheus (métricas)
- Grafana (dashboards)
- Jaeger (tracing)
- Loki (logs)

---

## 🏃 Como Executar

### 1️⃣ Pré-requisitos

```bash
# Ferramentas necessárias
- Docker Desktop
- Java 17
- Maven 3.8+
- Node.js 18+
- Git
```

### 2️⃣ Clonar e Preparar

```bash
# Clone o repositório
cd /Users/danielsmanioto/Documents/projects/projets_senior/venda-distribuida
```

### 3️⃣ Subir Infraestrutura

```bash
# Recomendado: usar script da raiz (sobe infra e valida serviços)
./start-all.sh

# Verificar se todos os containers estão rodando
docker-compose ps

# Aguardar ~30 segundos para inicialização completa
```

Alternativa (manual):

```bash
docker-compose up -d
```

**Serviços disponíveis:**
- Frontend (após `npm run dev`): http://localhost:5173
- PostgreSQL usuarios: localhost:5434
- PostgreSQL produtos-master: localhost:5435
- PostgreSQL produtos-replica: localhost:5437
- PostgreSQL vendas: localhost:5436
- Redis: localhost:6379
- RabbitMQ: localhost:5672 (AMQP) | localhost:15672 (UI)
- Kafka: localhost:9092
- Zookeeper: localhost:2181
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin123)
- Jaeger: http://localhost:16686

### 4️⃣ Executar Microserviços (Backend)

#### Terminal 1 - usuarios-service
```bash
cd usuarios
mvn spring-boot:run
```
✅ Disponível em: http://localhost:8080

#### Terminal 2 - produtos-write-service
```bash
cd produtos-write-service
mvn spring-boot:run
```
✅ Disponível em: http://localhost:8081

#### Terminal 3 - produtos-read-service
```bash
cd produtos-read-service
mvn spring-boot:run
```
✅ Disponível em: http://localhost:8082

#### Terminal 4 - vendas-service
```bash
cd vendas
mvn spring-boot:run
```
✅ Disponível em: http://localhost:8083

> Opcional (quando quiser validar build): `mvn clean install -DskipTests`

### 5️⃣ Executar Frontend

#### Terminal 5 - React Frontend
```bash
cd frontend
npm install
npm run dev
```
✅ Disponível em: http://localhost:5173

---

## 🧪 Testar o Sistema

### 1. Criar Usuário

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste User",
    "email": "teste@email.com",
    "senha": "senha123",
    "role": "USER"
  }'
```

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@email.com",
    "senha": "senha123"
  }'
```

Copie o `token` retornado.

### 3. Criar Produto (Write Service)

```bash
curl -X POST http://localhost:8081/api/produtos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "nome": "Notebook Dell",
    "descricao": "Notebook i7 16GB",
    "preco": 3500.00,
    "estoque": 10,
    "categoria": "Eletrônicos",
    "sku": "DELL-NB-001"
  }'
```

### 4. Listar Produtos (Read Service)

```bash
# Via API
curl http://localhost:8082/api/produtos

# Via Frontend
# Acesse http://localhost:5173
# Faça login
# Produtos aparecerão automaticamente
```

### 5. Criar Venda

```bash
curl -X POST http://localhost:8083/api/vendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
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

---

## 📊 Monitoramento

### Health Checks

```bash
# usuarios-service
curl http://localhost:8080/actuator/health

# produtos-write-service
curl http://localhost:8081/actuator/health

# produtos-read-service
curl http://localhost:8082/actuator/health

# vendas-service
curl http://localhost:8083/actuator/health
```

### Métricas Prometheus

```bash
# Acessar métricas de cada serviço
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus
curl http://localhost:8083/actuator/prometheus
```

### RabbitMQ Management

Acesse: http://localhost:15672
- Usuário: `admin`
- Senha: `admin123`

Verifique:
- Exchange: `produto.events`
- Queues: `produto.read.queue`
- Mensagens sendo processadas

### Kafka UI

```bash
# Listar tópicos
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Consumir mensagens do tópico venda.criada
docker exec -it kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic venda.criada \
  --from-beginning
```

### Redis Cache

```bash
# Acessar Redis CLI
docker exec -it redis redis-cli -a redis123

# Verificar chaves em cache
KEYS *

# Ver valor de uma chave
GET produtos::1
```

---

## 🎯 Fluxo Completo de Uso

### Frontend (Usuário Final)

1. **Acesse** http://localhost:5173
2. **Login** com credenciais criadas
3. **Visualize** catálogo de produtos
4. Produtos são carregados do **produtos-read-service** (cache Redis)

### Backend (Fluxo de Eventos)

#### Criar Produto
```
1. POST /api/produtos → produtos-write-service
2. Produto salvo em PostgreSQL Master
3. Evento publicado no RabbitMQ (produto.created)
4. produtos-read-service consome evento
5. Cache Redis invalidado
6. Próxima consulta busca do PostgreSQL Replica
7. Resultado armazenado em cache
```

#### Processar Venda
```
1. POST /api/vendas → vendas-service
2. Valida produtos via produtos-read-service (Circuit Breaker)
3. Calcula valor total
4. Salva venda em PostgreSQL
5. Publica evento no Kafka (venda.criada)
6. [Futuro] processa-venda-service consome evento
7. [Futuro] Atualiza estoque
8. [Futuro] Envia notificação
```

---

## 🛑 Parar Serviços

```bash
# Parar microserviços
# Ctrl+C em cada terminal

# Recomendado: usar script da raiz
./stop-all.sh

# Alternativa manual
docker-compose down

# Parar e remover volumes (CUIDADO: apaga dados)
docker-compose down -v
```

## 🚚 Deploy (produção)

```bash
# Script de deploy com docker-compose.prod.yml
./deploy.sh
```

> Observação: `deploy.sh` é para ambiente de produção/homologação. Para desenvolvimento local, use `./start-all.sh` e `./stop-all.sh`.

---

## 🐛 Troubleshooting

### Porta já em uso

```bash
# Verificar porta em uso (macOS)
lsof -i :8080
lsof -i :5434

# Matar processo
kill -9 PID
```

### Container não inicia

```bash
# Ver logs do container
docker logs postgres-usuarios
docker logs redis
docker logs rabbitmq
docker logs kafka

# Reiniciar container específico
docker-compose restart postgres-usuarios
```

### Erro de conexão com banco

```bash
# Verificar se PostgreSQL está aceitando conexões
docker exec -it postgres-usuarios psql -U admin -d usuarios

# Recriar banco de dados
docker-compose down -v
docker-compose up -d
```

### Cache não funciona

```bash
# Verificar Redis
docker exec -it redis redis-cli -a redis123 PING

# Limpar cache
docker exec -it redis redis-cli -a redis123 FLUSHALL
```

### Eventos não são consumidos

```bash
# Verificar RabbitMQ
# Acesse http://localhost:15672
# Verifique se há mensagens nas filas

# Verificar Kafka
docker exec -it kafka kafka-topics.sh --describe --topic venda.criada --bootstrap-server localhost:9092
```

---

## 📁 Estrutura do Projeto

```
venda-distribuida/
├── usuarios/                    # Serviço de usuários (8080)
├── produtos-write-service/      # CQRS Write (8081)
├── produtos-read-service/       # CQRS Read (8082)
├── vendas/                      # Serviço de vendas (8083)
├── frontend/                    # React Frontend (5173)
├── docker-compose.yml           # Infraestrutura
├── start-all.sh                 # Sobe infraestrutura local
├── stop-all.sh                  # Para infraestrutura local
├── deploy.sh                    # Deploy de produção
├── README.md                    # Documentação principal
├── PRODUTOS-README.md           # Documentação CQRS
└── docs/guias/GUIA-EXECUCAO.md  # Este arquivo
```

---

## 🎓 Padrões Implementados

- ✅ **CQRS** - Command Query Responsibility Segregation (produtos)
- ✅ **Event-Driven Architecture** - RabbitMQ e Kafka
- ✅ **Circuit Breaker** - Resilience4j em todos os serviços
- ✅ **Rate Limiting** - Controle de taxa de requisições
- ✅ **Cache-Aside Pattern** - Redis com invalidação por eventos
- ✅ **Database Replication** - Master-Replica PostgreSQL
- ✅ **JWT Authentication** - Segurança com tokens
- ✅ **Observability** - Prometheus, Grafana, Jaeger
- ✅ **Health Checks** - Spring Actuator

---

## 🚀 Próximos Passos

- [ ] Implementar API Gateway
- [ ] Criar processa-venda-service
- [ ] Criar notificacao-service
- [ ] Adicionar testes automatizados
- [ ] Implementar CI/CD
- [ ] Deploy em Kubernetes
- [ ] Adicionar autenticação OAuth2
- [ ] Implementar carrinho de compras no frontend

---

## 📝 Notas Importantes

1. **Ordem de inicialização importa**:
  - Sempre suba a infraestrutura primeiro (`./start-all.sh`)
   - Aguarde ~30 segundos antes de subir os microserviços
   - Suba os microserviços na ordem: usuarios → produtos-write → produtos-read → vendas
   - Por último, suba o frontend

2. **CORS**:
  - Todos os backends têm CORS habilitado para `http://localhost:5173`
   - Em produção, configure CORS adequadamente

3. **Segurança**:
   - Senhas e credenciais são para desenvolvimento
   - Em produção, use secrets management (Vault, AWS Secrets Manager)

4. **Performance**:
   - Cache Redis configurado com TTL de 1 hora
   - Circuit Breaker abre após 50% de falhas
   - Rate Limiter permite 100 req/s

5. **Dados**:
   - Banco de dados é criado automaticamente na primeira execução
   - Dados são persistidos em volumes Docker
   - Use `docker-compose down -v` para limpar tudo

---

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs: `docker-compose logs -f`
2. Consulte a seção de Troubleshooting
3. Verifique health checks dos serviços
4. Revise as configurações em application.yml de cada serviço
