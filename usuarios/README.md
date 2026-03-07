# 👤 usuarios-service

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-111827?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge" alt="JWT" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
</p>

Microserviço responsável por **autenticação** e **gestão de usuários**.

---

## ✨ Funcionalidades

- Registro de usuário
- Login com JWT
- CRUD de usuários
- Roles `USER` e `ADMIN`
- Resiliência com Resilience4j
- Observabilidade com Actuator + Prometheus

---

## 🚀 Executar localmente

```bash
mvn clean package
mvn spring-boot:run
```

Porta padrão: `8080`

---

## ⚙️ Variáveis importantes

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/usuarios
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123
JWT_SECRET=sua-chave-secreta
```

---

## 📡 Endpoints

### Públicos

- `POST /api/auth/registro`
- `POST /api/auth/login`

### Protegidos

- `GET /api/usuarios/{id}`
- `GET /api/usuarios` (admin)
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}` (admin)

### Actuator

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

---

## 🛡️ Segurança e Resiliência

- Spring Security + JWT
- Senhas com BCrypt
- Circuit Breaker
- Rate Limiter
- Retry com backoff

---

## 📈 Observabilidade

- Logs: `logs/usuarios-service.log`
- Prometheus: `http://localhost:8080/actuator/prometheus`
- Jaeger: `http://localhost:16686`

---

## 🧪 Testes

```bash
mvn test
```
