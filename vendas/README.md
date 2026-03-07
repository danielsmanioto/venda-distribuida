# 💳 venda-service

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-111827?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.13-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Kafka-Events-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
</p>

Microserviço responsável pelo **ciclo de vendas**, integração com catálogo e publicação de eventos.

---

## ✨ Funcionalidades

- Criar venda
- Consultar venda por ID
- Listar vendas por usuário
- Cancelar venda
- Publicar eventos no Kafka

---

## 🚀 Executar localmente

```bash
mvn clean package
mvn spring-boot:run
```

Porta padrão: `8083`

---

## ⚙️ Configuração essencial

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5436/vendas
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SERVICES_PRODUTOS_URL=http://localhost:8082
```

---

## 📡 Endpoints

- `POST /api/vendas`
- `GET /api/vendas/{id}`
- `GET /api/vendas/usuario/{usuarioId}`
- `GET /api/vendas?page=0&size=20`
- `PUT /api/vendas/{id}/cancelar`

---

## 🧭 Eventos Kafka

- `venda.criada`
- `venda.processada`
- `venda.cancelada`

---

## 📈 Observabilidade

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`
- Jaeger: `http://localhost:16686`

---

## 🧪 Testes

```bash
mvn test
```

---

## 📋 Status de venda

- `PENDENTE`
- `CONFIRMADA`
- `CANCELADA`
