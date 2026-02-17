# Usuarios Service - cURL Examples

## 1️⃣ REGISTRO - Criar novo usuário
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "senha": "Senha123!"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "role": "USER"
}
```

---

## 2️⃣ LOGIN - Obter JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "senha": "Senha123!"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@example.com",
    "role": "USER"
  }
}
```

---

## 3️⃣ BUSCAR USUÁRIO - Por ID (autenticado)
```bash
# Substituir TOKEN pelo token obtido no login
TOKEN="seu_jwt_token_aqui"

curl -X GET http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "role": "USER"
}
```

---

## 4️⃣ LISTAR TODOS OS USUÁRIOS (apenas ADMIN)
```bash
TOKEN="seu_jwt_token_admin_aqui"

curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@example.com",
    "role": "USER"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "email": "maria@example.com",
    "role": "USER"
  }
]
```

---

## 5️⃣ ATUALIZAR USUÁRIO (autenticado)
```bash
TOKEN="seu_jwt_token_aqui"

curl -X PUT http://localhost:8080/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "João Silva Updated",
    "email": "joao.updated@example.com",
    "senha": "NovaSenha123!"
  }'
```

**Resposta esperada:**
```json
{
  "id": 1,
  "nome": "João Silva Updated",
  "email": "joao.updated@example.com",
  "role": "USER"
}
```

---

## 6️⃣ DELETAR USUÁRIO (apenas ADMIN)
```bash
TOKEN="seu_jwt_token_admin_aqui"

curl -X DELETE http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
- Status: 204 No Content (sem corpo)

---

## 🔐 CHEAT SHEET - Fluxo completo

### Passo 1: Registrar
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Test User",
    "email": "test@example.com",
    "senha": "Test123!"
  }'
```

### Passo 2: Login (salvar o TOKEN)
```bash
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "senha": "Test123!"
  }')

# Extrair o token (se tiver jq instalado)
TOKEN=$(echo $RESPONSE | jq -r '.token')
echo "Token: $TOKEN"
```

### Passo 3: Usar o token
```bash
# Buscar próprio usuário
curl -X GET http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"

# Atualizar
curl -X PUT http://localhost:8080/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Test User Updated",
    "email": "test.updated@example.com",
    "senha": "Test123!"
  }'
```

---

## 📊 Validações e Erros

### Erro: Usuário não autenticado (sem Token)
```bash
curl -X GET http://localhost:8080/api/usuarios/1
```
**Resposta:**
```json
{
  "error": "Unauthorized",
  "status": 401
}
```

### Erro: Permissão insuficiente (USER tentando listar todos)
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $USER_TOKEN"
```
**Resposta:**
```json
{
  "error": "Forbidden",
  "status": 403
}
```

### Erro: Email já existe
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Duplicado",
    "email": "joao@example.com",
    "senha": "Senha123!"
  }'
```
**Resposta:**
```json
{
  "error": "Email já registrado",
  "status": 409
}
```

---

## 💡 DICAS

### Com jq (para formatar JSON):
```bash
curl -s http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Com arquivo de request:
```bash
# Criar arquivo: request.json
cat > request.json << 'EOF'
{
  "nome": "João",
  "email": "joao@example.com",
  "senha": "Senha123!"
}
EOF

# Usar no curl
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d @request.json
```

### Com variáveis de ambiente:
```bash
export API_URL="http://localhost:8080"
export TOKEN="seu_token_aqui"

curl -X GET $API_URL/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## ✅ Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Resposta:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```
