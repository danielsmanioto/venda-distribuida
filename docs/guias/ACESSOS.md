# 🔐 Acessos e Credenciais (Ambiente Local)

Este documento centraliza as URLs e credenciais usadas no ambiente local do projeto.

---

## 🌐 URLs de Acesso

| Serviço | URL | Observação |
|---------|-----|------------|
| Frontend (Vite) | http://localhost:5173 | Rodar com `npm run dev` em `frontend/` |
| Usuários API | http://localhost:8080 | Serviço de autenticação e usuários |
| Produtos Write API | http://localhost:8081 | Comandos (CQRS write) |
| Produtos Read API | http://localhost:8082 | Consultas (CQRS read) |
| Vendas API | http://localhost:8083 | Processamento de vendas |
| Swagger Usuários | http://localhost:8080/swagger-ui.html | Documentação da API |
| Swagger Produtos | http://localhost:8081/swagger-ui.html | Documentação da API |
| Swagger Vendas | http://localhost:8083/swagger-ui.html | Documentação da API |
| RabbitMQ Management | http://localhost:15672 | Console do RabbitMQ |
| Kafka UI | http://localhost:8089 | Console de tópicos Kafka |
| Prometheus | http://localhost:9090 | Métricas |
| Grafana | http://localhost:3000 | Dashboards |
| Jaeger | http://localhost:16686 | Tracing distribuído |
| Loki | http://localhost:3100 | Logs |

---

## 👤 Credenciais

### Grafana
- Usuário: `admin`
- Senha: `admin123`

### RabbitMQ
- Usuário: `admin`
- Senha: `admin123`

### PostgreSQL (todas as instâncias)
- Usuário: `admin`
- Senha: `admin123`

Instâncias e portas:
- `postgres-usuarios`: `localhost:5434` (db: `usuarios`)
- `postgres-produtos-master`: `localhost:5435` (db: `produtos`)
- `postgres-produtos-replica`: `localhost:5437` (db: `produtos`)
- `postgres-vendas`: `localhost:5436` (db: `vendas`)

### Redis
- Host: `localhost`
- Porta: `6379`
- Senha: `redis123`

---

## ▶️ Observações rápidas

- `start-all.sh` sobe a infraestrutura Docker e valida serviços.
- `stop-all.sh` derruba os containers da infraestrutura.
- O frontend **não** roda no Docker Compose deste fluxo local; ele é iniciado no modo dev via Vite (`frontend`).
