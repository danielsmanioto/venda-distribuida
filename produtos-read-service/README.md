# 📖 produtos-read-service

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-111827?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/CQRS-Query-0EA5E9?style=for-the-badge" alt="CQRS Query" />
  <img src="https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
</p>

Microserviço de **leitura** do domínio de produtos, com cache Redis e consumo de eventos para atualização de dados.

---

## ✨ Funcionalidades

- Buscar produto por ID
- Listar produtos (paginado)
- Buscar por categoria
- Buscar por SKU
- Cache de consultas com Redis

---

## 🚀 Executar localmente

```bash
mvn clean package
mvn spring-boot:run
```

Porta padrão: `8082`

---

## ⚙️ Variáveis essenciais

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5437/produtos
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
```

---

## 📡 Endpoints

- `GET /api/produtos/{id}`
- `GET /api/produtos`
- `GET /api/produtos/todos`
- `GET /api/produtos/categoria/{categoria}`
- `GET /api/produtos/buscar`
- `GET /api/produtos/sku/{sku}`

---

## 📈 Observabilidade

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`
- `GET /actuator/caches`

Logs: `logs/produtos-read-service.log`
