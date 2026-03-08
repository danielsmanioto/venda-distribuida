# Usuários - API & CURL Examples

Endpoints do `usuarios-service` (porta 8080) e exemplos curl para testar localmente.

Base URL: http://localhost:8080

## Autenticação

### Registrar usuário
```bash
curl -s -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "role": "USER"
  }'
```

### Login (obter token)
```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'

# resposta esperada (exemplo):
# { "token": "<JWT>", "tipo": "Bearer", "id": 1, "nome": "João Silva", "email": "..." }
```

Guarde o token retornado e defina a variável `TOKEN` para usar nos exemplos abaixo:

```bash
TOKEN="eyJhbGciOi..."
```

## Endpoints de Usuários

### Criar usuário (endpoint direto de usuários)
Request body (`UsuarioRequest`) exemplo: `{ "nome", "email", "senha", "role" }`

```bash
curl -s -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Ana Pereira",
    "email": "ana@email.com",
    "senha": "senha123",
    "role": "USER"
  }'
```

Resposta: 201 Created com o `UsuarioResponse` JSON.

### Listar usuários
```bash
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN"
```

### Buscar usuário por ID
```bash
curl -s -X GET http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Atualizar usuário
Request body (`UsuarioRequest` usado para atualização):

```bash
curl -s -X PUT http://localhost:8080/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "João Silva Atualizado",
    "email": "joao@email.com",
    "senha": "novaSenha123",
    "role": "USER"
  }'
```

### Deletar (desativar) usuário
```bash
curl -s -X DELETE http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Health & Metrics

### Health
```bash
curl -s http://localhost:8080/actuator/health | jq
```

### Prometheus metrics (actuator)
```bash
curl -s http://localhost:8080/actuator/prometheus | head -n 50
```

## Fluxo de teste rápido (exemplo)

1) Registrar e logar (pegar token)

```bash
curl -s -X POST http://localhost:8080/api/auth/registro -H "Content-Type: application/json" -d '{"nome":"Teste","email":"teste@local","senha":"123456","role":"USER"}'

RESP=$(curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"teste@local","senha":"123456"}')
TOKEN=$(echo "$RESP" | jq -r .token)

echo "Token: $TOKEN"
```

2) Criar e listar usuários

```bash
curl -s -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d '{"nome":"Usu Test","email":"usu@local","senha":"123","role":"USER"}'

curl -s -X GET http://localhost:8080/api/usuarios -H "Authorization: Bearer $TOKEN" | jq
```

---

Se quiser, eu comito esse arquivo `docs/apis/USUARIOS-CURL.md` no repositório (e atualizo `docs/apis/INDEX.md` se existir). Quer que eu faça o commit e push? 
