# ✍️ produtos-write-service

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-111827?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/CQRS-Command-blueviolet?style=for-the-badge" alt="CQRS Command" />
  <img src="https://img.shields.io/badge/RabbitMQ-Events-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ" />
</p>

Microserviço de **escrita** do domínio de produtos: criação, atualização, remoção e operações de estoque.

---

## ✨ Funcionalidades

- Criar produto
- Atualizar produto
- Excluir produto
- Entrada/saída de estoque
- Publicação de eventos para sincronização/cache

---

## 🚀 Executar localmente

```bash
mvn clean package
mvn spring-boot:run
```

Porta padrão: `8081`

---

## ⚙️ Variáveis essenciais

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5435/produtos
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

---

## 📡 Endpoints

- `POST /api/produtos`
- `PUT /api/produtos/{id}`
- `DELETE /api/produtos/{id}`
- `POST /produtos/{id}/estoque/entrada`
- `POST /produtos/{id}/estoque/saida`
- `GET /produtos/{id}/estoque/saldo`
- `GET /produtos/{id}/estoque/historico`

---

## 📈 Observabilidade

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

Logs: `logs/produtos-write-service.log`
