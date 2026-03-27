# 🛒 Venda Distribuída

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:0EA5E9,100:7C3AED&height=220&section=header&text=Venda%20Distribu%C3%ADda&fontColor=ffffff&fontSize=52&fontAlignY=38&desc=Microservi%C3%A7os%20com%20CQRS%20+%20Event-Driven%20+%20Observabilidade&descAlignY=60" alt="Venda Distribuída" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-111827?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 25" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

Sistema de e-commerce distribuído com foco em **resiliência**, **escala** e **observabilidade**.

---

## 📌 Índice

- [🎯 Visão Geral](#-visão-geral)
- [🏗️ Arquitetura](#️-arquitetura)
- [📊 Requisitos do Sistema](#-requisitos-do-sistema)
- [🔧 Microserviços](#-microserviços)
- [🗄️ Infraestrutura](#️-infraestrutura)
- [🎨 Padrões Arquiteturais](#-padrões-arquiteturais)
- [📈 Observabilidade](#-observabilidade)
- [🚀 Como Executar](#-como-executar)
- [🛠️ Troubleshooting Local](#️-troubleshooting-local)
- [🌐 URLs de Acesso](#-urls-de-acesso)

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
| **Frontend (dev / Vite)** | http://localhost:5173 | Interface do usuário (MFE) |
| **API Gateway** | http://localhost/api | Ponto de entrada principal da API |
| **Swagger - Usuários** | http://localhost:8080/swagger-ui.html | Documentação API de usuários |
| **Swagger - Produtos** | http://localhost:8081/swagger-ui.html | Documentação API de produtos |
| **Swagger - Vendas** | http://localhost:8083/swagger-ui.html | Documentação API de vendas |
| **Prometheus** | http://localhost:9090 | Métricas do sistema |
| **Grafana** | http://localhost:3000 | Dashboards e visualizações |
| **Jaeger** | http://localhost:16686 | Tracing distribuído |
| **Loki** | http://localhost:3100 | Agregação de logs |
| **RabbitMQ Management** | http://localhost:15672 | Console de gerenciamento RabbitMQ |
| **Kafka UI** | http://localhost:8089 | Interface para visualizar tópicos Kafka |

### Credenciais Padrão

| Serviço | Usuário | Senha |
|---------|---------|-------|
| **Grafana** | admin | admin123 |
| **RabbitMQ** | admin | admin123 |
| **PostgreSQL (todos os bancos)** | admin | admin123 |
| **Redis** | - | redis123 |

Para a lista consolidada de acessos e credenciais locais, consulte [docs/guias/ACESSOS.md](docs/guias/ACESSOS.md).

---

## 🚀 Como Executar

### ⚡ Início Rápido Local (recomendado)

Se você quer subir o projeto local em poucos minutos, use os scripts da raiz:

```bash
# 1) Na raiz do projeto
cd /Users/danielsmanioto/Documents/projects/projets_senior/venda-distribuida

# 2) Dar permissão de execução (somente na primeira vez)
chmod +x start-all.sh stop-all.sh deploy.sh

# 3) Subir infraestrutura local (Docker)
./start-all.sh
```

Depois disso, inicie os serviços de aplicação em terminais separados (como o script orienta):

```bash
# Terminal 1
cd usuarios && mvn spring-boot:run

# Terminal 2
cd produtos-write-service && mvn spring-boot:run

# Terminal 3
cd produtos-read-service && mvn spring-boot:run

# Terminal 4
cd vendas && mvn spring-boot:run

# Terminal 5
cd frontend && npm install && npm run dev
```

Para parar a infraestrutura:

```bash
./stop-all.sh
```

Observações:
- `start-all.sh`: sobe Docker Compose e valida infraestrutura.
- `stop-all.sh`: derruba os containers.
- `deploy.sh`: fluxo de deploy para ambiente de produção com `docker-compose.prod.yml` (não é o caminho padrão para desenvolvimento local).
- Para passo a passo completo e testes via cURL, veja [docs/guias/GUIA-EXECUCAO.md](docs/guias/GUIA-EXECUCAO.md).

### Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+
- 8GB RAM disponível
- Portas disponíveis: 5173, 8080-8085, 5434-5437, 6379, 9090, 3000, 3100, 16686

---

## 🛠️ Troubleshooting Local

### 1) Frontend abre o Grafana em vez da aplicação

Isso acontece por conflito de porta (`3000`).

- Frontend (Vite) deste projeto: `http://localhost:5173`
- Grafana: `http://localhost:3000`

Se o frontend não subir em `5173`, confira a saída do terminal do `npm run dev` e abra a URL `Local:` exibida.

### 2) Porta já em uso

No macOS, para identificar processo em uma porta:

```bash
lsof -i :5173
lsof -i :3000
lsof -i :8080
```

Se necessário, encerre o processo e suba novamente o serviço.

### 3) Containers não iniciam corretamente

```bash
docker-compose ps
docker-compose logs --tail=100
```

Se quiser limpar tudo e subir do zero:

```bash
docker-compose down -v
docker-compose up -d
```

### 4) Roteiro recomendado para evolução em observabilidade

Para praticar métricas, logs, traces e montar investigação ponta a ponta, siga:

- [docs/guias/OBSERVABILIDADE.md](docs/guias/OBSERVABILIDADE.md)

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
# Frontend: http://localhost:5173
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

### 🎯 Desenvolvimento com VS Code (Igual IntelliJ)

Você pode usar Visual Studio Code como IDE completa para desenvolvimento Java, com as mesmas funcionalidades do IntelliJ. Segue o guia:

#### **1. Requisitos Prévios**

- ✅ Java 25 instalado ([Download](https://www.oracle.com/java/technologies/downloads/#java25))
- ✅ VS Code instalado
- ✅ Node.js 18+ (para o frontend)

#### **2. Instalação de Extensões (Obrigatório)**

Abra VS Code e instale as seguintes extensões:

| Extensão | ID | Funcionalidade |
|----------|--|-|
| **Extension Pack for Java** | `vscjava.vscode-java-pack` | Java, Debugging, Maven, Test Runner |
| **Spring Boot Extension Pack** | `vmware.vscode-boot-dev-pack` | Spring Boot, Cloud, Dashboard |
| **Maven for Java** | `vscjava.vscode-maven` | Build, Run, Test |

**Via Terminal**:
```bash
code --install-extension vscjava.vscode-java-pack
code --install-extension vmware.vscode-boot-dev-pack
code --install-extension vscjava.vscode-maven
```

#### **3. Configuração Inicial**

Crie o arquivo `.vscode/settings.json` na raiz do projeto:

```json
{
  "java.jdt.ls.vmargs": "-XX:+UseStringDeduplication -XX:+UseG1GC -XX:+UseGCOverheadLimit -Xmx2G",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "/path/to/java-25",
      "default": true
    }
  ],
  "java.import.maven.enabled": true,
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
      "source.organizeImports": "explicit"
    }
  },
  "spring-boot.fast-start": true
}
```

**📌 No macOS**, o caminho do Java é geralmente:
```
/Users/seu-usuario/Documents/java/jdk-25.0.1.jdk/Contents/Home
```

#### **4. Executando os Serviços**

**Método 1: Via Paleta de Comandos (Recomendado)**
- Pressione `Cmd+Shift+P` → busque por `Java: Run`
- Selecione `UsuariosServiceApplication` ou o serviço desejado
- Clique em `Run`

**Método 2: Via Spring Boot Dashboard**
- Abra a aba de Explorador (`Cmd+Shift+E`)
- Procure por **Spring Boot Dashboard**
- Clique em ▶️ ao lado do serviço para iniciar

**Método 3: Via Terminal Maven**
```bash
# Dentro da pasta do serviço (ex: usuarios/)
mvn spring-boot:run

# Ou diretamente do root com módulo específico
mvn -pl usuarios spring-boot:run
```

**Método 4: Debug Mode (como no IntelliJ)**
1. Abra arquivo `UsuariosServiceApplication.java`
2. Clique no botão **Debug** acima do método `main`
3. Escolha a configuração de debug
4. Use `F10` para step over, `F11` para step into

#### **5. Rodando os Testes**

**Executar Testes Unitários**:
```bash
# Todos os testes
mvn test

# Testes de um serviço específico
mvn -pl usuarios test

# Teste específico
mvn -pl usuarios test -Dtest=UsuarioServiceTest
```

**Ou via VS Code**:
- Clique no botão **Test Explorer** na barra lateral
- Procure pelo teste desejado
- Clique em ▶️ para executar

#### **6. Debugando como no IntelliJ**

**Adicionar Breakpoint**:
- Clique na linha desejada (margem esquerda)
- Um ponto vermelho 🔴 aparecerá

**Controles de Debug**:
| Atalho | Ação |
|--------|------|
| `F10` | Step Over (próxima linha) |
| `F11` | Step Into (entrar na função) |
| `Shift+F11` | Step Out (sair da função) |
| `F5` / `Cmd+Shift+D` | Continue (até próximo breakpoint) |
| `Shift+F5` | Stop debug |

**Inspecionar Variáveis**:
- Quando em pausa (breakpoint), use a aba **Variables** na lateral esquerda
- Passe o mouse sobre qualquer variável no código para preview

#### **7. Spring Boot Dashboard**

Funcionalidade exclusiva do VS Code com extensão Spring Boot:

1. Abra a aba **Spring Boot Dashboard** (ícone de folha verde)
2. Veja todos os serviços Spring Boot no projeto
3. **Iniciar/Parar**: Clique em ▶️ ou ⏹️
4. **Logs**: Clique em um serviço para ver logs em tempo real
5. **Endpoints**: Veja todos os endpoints disponíveis
6. **Actuator**: Acesse métricas de health em tempo real

#### **8. Extensões Úteis Adicionais**

```bash
# Thêm mais produtividade ao desenvolvimento

# Prettier - Code formatter
code --install-extension esbenp.prettier-vscode

# REST Client - Testar APIs
code --install-extension humao.rest-client

# GitLens - Integração com Git avançada
code --install-extension eamodio.gitlens

# Docker - Integração com containers
code --install-extension ms-azuretools.vscode-docker
```

#### **9. Executando a Infraestrutura**

```bash
# Na raiz do projeto, inicie os containers da infraestrutura
docker-compose up -d

# Verifique se está rodando
docker-compose ps

# Ver logs de um serviço
docker-compose logs -f postgres-usuarios

# Parar infraestrutura
docker-compose down
```

#### **10. Testando Endpoints**

**Via REST Client Extension** (`humao.rest-client`):

Crie arquivo `test.http` na raiz:
```http
### Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "senha": "123456"
}

### Listar Produtos
GET http://localhost:8081/api/produtos
Authorization: Bearer {{token}}

### Criar Venda
POST http://localhost:8083/api/vendas
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "usuarioId": 1,
  "produtoId": 1,
  "quantidade": 2
}
```

Depois clique em `Send Request` acima de cada requisição.

#### **11. Troubleshooting**

| Problema | Solução |
|----------|---------|
| "Java Runtime not found" | Confirme o caminho em `settings.json` com `java -version` no terminal |
| "Maven not found" | Execute `mvn --version` para confirmar instalação |
| "Port already in use" | Use `lsof -i :8080` para encontrar o processo e mate-o |
| "Connection refused" | Certifique-se de que `docker-compose up -d` foi executado |
| Debug não funciona | Limpe cache: `Cmd+Shift+P` → `Java: Clean Language Server Workspace` |

#### **12. Comparação: VS Code vs IntelliJ**

| Feature | VS Code | IntelliJ |
|---------|---------|----------|
| **Startup** | ⚡ Muito rápido | 🐢 Lento |
| **RAM** | 💾 Leve (500MB) | 💾 Pesado (2GB+) |
| **Debugging** | ✅ Completo | ✅ Completo |
| **Refactoring** | ✅ Bom | ✅ Excelente |
| **Autocomplete** | ✅ Muito bom | ✅ Perfeito |
| **Spring Boot** | ✅ Dashboard visual | ✅ Integrado |
| **Customização** | ✅ Extrema | ❌ Limitada |
| **Preço** | 🆓 Grátis | 💰 Pago |
| **Extensões** | ✅ Ecossistema grande | ❌ Built-in |

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



## 👥 Documentação

- **Documentacao Arquitetura**: [Doc](https://github.com/danielsmanioto/software_architecture_challenges/blob/main/sistema-venda-distribuido)
---



## 👥 Time

- **Tech Lead**: [Seu Nome]
- **Backend**: [Time Backend]
- **Frontend**: [Time Frontend]
- **DevOps**: [Time DevOps]
---

<div align="center">

**Feito com ❤️ pelo Time de Engenharia**

[🔝 Voltar ao topo](#-sistema-de-venda-online)

</div>
