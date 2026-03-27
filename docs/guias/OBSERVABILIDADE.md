# 📈 Guia Prático de Observabilidade

Objetivo: evoluir sua maturidade em observabilidade no projeto, de forma prática e incremental.

---

## 🎯 O que você vai treinar

- Métricas (RED + latência p95/p99)
- Logs estruturados e investigação por correlação
- Tracing distribuído entre serviços
- Diagnóstico de incidentes com evidências

---

## 1) Subir stack de observabilidade

Infraestrutura:

```bash
./start-all.sh
```

Serviços de aplicação (em terminais separados):

```bash
cd usuarios && mvn spring-boot:run
cd produtos-write-service && mvn spring-boot:run
cd produtos-read-service && mvn spring-boot:run
cd vendas && mvn spring-boot:run
```

Frontend (se necessário):

```bash
cd frontend && npm install && npm run dev
```

---

## 2) Pontos de acesso

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Jaeger: http://localhost:16686
- Loki: http://localhost:3100
- RabbitMQ: http://localhost:15672
- Kafka UI: http://localhost:8089

Credenciais locais:
- Grafana: `admin` / `admin123`
- RabbitMQ: `admin` / `admin123`

---

## 3) Checklist de saúde inicial

Valide endpoints de health:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

Valide métricas expostas:

```bash
curl http://localhost:8080/actuator/prometheus | head
curl http://localhost:8081/actuator/prometheus | head
curl http://localhost:8082/actuator/prometheus | head
curl http://localhost:8083/actuator/prometheus | head
```

---

## 4) Trilha prática (7 dias)

### Dia 1 — Baseline
- Registrar latência média e p95 dos endpoints principais.
- Registrar taxa de erro por serviço.

### Dia 2 — Dashboards
- Criar dashboard de “Visão Geral” no Grafana com:
  - Requisições/s
  - Erros/s
  - Latência p95
  - Status `UP` dos serviços

### Dia 3 — Logs
- Definir padrão de log com campos mínimos:
  - `timestamp`, `level`, `service`, `traceId`, `spanId`, `message`
- Validar busca por erro no Loki.

### Dia 4 — Tracing
- Gerar fluxo de negócio (login → consulta produto → venda).
- Encontrar o trace completo no Jaeger.
- Medir gargalo principal do fluxo.

### Dia 5 — Filas e assíncrono
- Monitorar fila/tópico (RabbitMQ/Kafka).
- Acompanhar delay de processamento após pico de carga.

### Dia 6 — Incidente controlado
- Simular falha de dependência (parar um serviço localmente).
- Verificar:
  - alterações em métricas
  - mensagens de erro em logs
  - spans com erro no trace

### Dia 7 — Playbook
- Documentar um playbook com:
  - alerta disparado
  - sinais-chave
  - hipótese
  - causa raiz
  - ação corretiva

---

## 5) Consultas e sinais úteis

### Prometheus (exemplos)

Taxa de requisições por serviço:

```promql
sum(rate(http_server_requests_seconds_count[5m])) by (application)
```

Taxa de erro (5xx):

```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (application)
```

Latência p95:

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, application))
```

### Investigação (fluxo recomendado)

1. Alerta em métrica (Prometheus/Grafana)
2. Identificar serviço e janela de tempo
3. Buscar logs correlacionados (Loki)
4. Abrir trace da mesma janela (Jaeger)
5. Confirmar causa e aplicar correção

---

## 6) Meta de maturidade para este projeto

Busque atingir estes 4 resultados:

1. **Detecção rápida:** perceber degradação em até 5 minutos.
2. **Diagnóstico guiado:** localizar serviço e causa em até 15 minutos.
3. **Correlação completa:** métrica + log + trace para incidentes críticos.
4. **Prevenção:** alertas para latência, erro e indisponibilidade.

---

## 7) Próximos passos recomendados

- Criar alertas no Grafana para:
  - disponibilidade (`UP`)
  - p95 acima do limite
  - taxa de erro acima de limiar
- Definir SLO por serviço (ex.: disponibilidade e latência).
- Versionar dashboards (JSON) no repositório.
