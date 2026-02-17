# Usuários Service

Microserviço de autenticação e gerenciamento de usuários do sistema de venda distribuída.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Security + JWT**
- **PostgreSQL**
- **Resilience4j** (Circuit Breaker, Rate Limiter, Retry)
- **Micrometer** (Métricas e Tracing)
- **Prometheus & Grafana** (Observabilidade)
- **Jaeger** (Distributed Tracing)

## 📋 Funcionalidades

- ✅ Registro de novos usuários
- ✅ Autenticação com JWT
- ✅ CRUD de usuários
- ✅ Autorização baseada em roles (USER, ADMIN)
- ✅ Circuit Breaker para resiliência
- ✅ Rate Limiting
- ✅ Métricas expostas para Prometheus
- ✅ Tracing distribuído com Jaeger
- ✅ Health checks

## 🏗️ Arquitetura

```
usuarios-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/vendadistribuida/usuarios/
│   │   │       ├── config/           # Configurações
│   │   │       ├── controller/       # Controllers REST
│   │   │       ├── domain/
│   │   │       │   ├── dto/         # DTOs
│   │   │       │   └── entity/      # Entidades JPA
│   │   │       ├── exception/        # Exception handlers
│   │   │       ├── repository/       # Repositories JPA
│   │   │       ├── security/         # JWT e Security
│   │   │       └── service/          # Serviços
│   │   └── resources/
│   │       └── application.yml       # Configurações
│   └── test/                         # Testes
├── Dockerfile
└── pom.xml
```

## 🔧 Configuração

### Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (ou Docker)

### Variáveis de Ambiente

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/usuarios
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123

# JWT
JWT_SECRET=sua-chave-secreta-muito-segura

# Observability
MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://localhost:16686/api/traces
```

## 🚀 Executando

### Com Maven

```bash
# Compilar
mvn clean package

# Executar
mvn spring-boot:run
```

### Com Docker

```bash
# Build
docker build -t usuarios-service:latest .

# Run
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5434/usuarios \
  usuarios-service:latest
```

### Com Docker Compose (na raiz do projeto)

```bash
cd ../
docker-compose up postgres-usuarios -d
cd usuarios
mvn spring-boot:run
```

## 📡 Endpoints

### Autenticação (Públicos)

```http
POST /api/auth/registro
Content-Type: application/json

{
  "email": "user@example.com",
  "senha": "senha123",
  "nome": "João Silva",
  "cpf": "12345678900",
  "telefone": "11999999999"
}
```

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "senha": "senha123"
}
```

### Usuários (Requer autenticação)

```http
GET /api/usuarios/{id}
Authorization: Bearer {token}
```

```http
GET /api/usuarios
Authorization: Bearer {token}
# Requer ROLE_ADMIN
```

```http
PUT /api/usuarios/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "user@example.com",
  "nome": "João Silva Santos",
  "cpf": "12345678900",
  "telefone": "11999999999"
}
```

```http
DELETE /api/usuarios/{id}
Authorization: Bearer {token}
# Requer ROLE_ADMIN
```

### Observabilidade

```http
GET /actuator/health         # Health check
GET /actuator/metrics        # Métricas
GET /actuator/prometheus     # Métricas formato Prometheus
GET /actuator/circuitbreakers # Status circuit breakers
```

## 📊 Métricas e Monitoramento

### Prometheus

Métricas disponíveis em: `http://localhost:8080/actuator/prometheus`

Métricas customizadas:
- `auth.registro` - Tempo de registro
- `auth.login` - Tempo de login
- `usuarios.buscar` - Tempo de busca
- `usuarios.listar` - Tempo de listagem
- `usuarios.atualizar` - Tempo de atualização
- `usuarios.deletar` - Tempo de deleção

### Grafana

Dashboard sugerido:
- JVM metrics
- HTTP requests
- Circuit breaker status
- Database connection pool

### Jaeger

Tracing distribuído disponível em: `http://localhost:16686`

## 🛡️ Resilience4j

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

## 🔐 Segurança

- Senhas criptografadas com BCrypt
- Autenticação JWT
- CORS configurável
- Autorização baseada em roles
- Validação de entrada com Bean Validation

## 🧪 Testes

```bash
# Executar testes
mvn test

# Executar com cobertura
mvn test jacoco:report
```

## 📝 Logs

Logs são salvos em: `logs/usuarios-service.log`

Níveis de log configuráveis via `application.yml`

## 🐛 Troubleshooting

### Problema: Erro de conexão com PostgreSQL

**Solução**: Verifique se o PostgreSQL está rodando:
```bash
docker-compose ps postgres-usuarios
```

### Problema: Token JWT inválido

**Solução**: Verifique se a secret está configurada corretamente

### Problema: Circuit breaker aberto

**Solução**: Verifique os logs e aguarde o tempo de espera configurado

## 📚 Documentação Adicional

- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [Resilience4j](https://resilience4j.readme.io/)
- [Micrometer](https://micrometer.io/)

## 👥 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.
