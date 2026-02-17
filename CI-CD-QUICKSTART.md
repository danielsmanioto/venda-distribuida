# 🚀 Quick Start - CI/CD Pipeline

Instruções rápidas para ativar a pipeline de CI/CD automática.

## ⚡ 5 Passos para Ativar

### 1️⃣ Crie um Personal Access Token no GitHub

```
1. GitHub → Settings → Developer settings → Personal access tokens
2. Click "Generate new token"
3. Nome: "venda-distribuida-deploy"
4. Permissões necessárias:
   - [x] repo (full control)
   - [x] write:packages (push containers)
   - [x] read:packages (pull containers)
5. Copie o token gerado
```

### 2️⃣ Configure os Secrets do Repositório

```
1. Repository → Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Adicione:
   - GITHUB_TOKEN: <token-criado-no-passo-1>
```

### 3️⃣ Crie os Ambientes (optional)

```
1. Repository → Settings → Environments
2. Click "New environment"
3. Crie dois ambientes:
   - "staging" (sem aprovação)
   - "production" (com aprovação manual)
```

### 4️⃣ Configure Branch Protection

```
1. Repository → Settings → Branches
2. Click "Add rule" (ou edite "main")
3. Configure:
   - [x] Require a pull request before merging
   - [x] Require status checks to pass
   - [x] Require branches to be up to date
   - [x] Dismiss stale pull request approvals
```

### 5️⃣ Faça um Push e Veja a Magia Acontecer

```bash
git add .
git commit -m "chore: setup CI/CD pipeline"
git push origin main

# Acesse: Repository → Actions → CI/CD - Venda Distribuída
# Acompanhe o progresso em tempo real!
```

## 📊 O que Acontece Automaticamente

### No Pull Request
- ✅ Testes rodando em paralelo
- ✅ Verificações de segurança
- ✅ Cobertura de testes
- ✅ Build de imagens (preview)

### No Merge para `develop`
- ✅ Testes executados
- ✅ Build de imagens Docker
- ✅ Push para GitHub Container Registry
- ✅ Deploy automático para staging

### No Merge para `main`
- ✅ Testes executados
- ✅ Build de imagens Docker
- ✅ Push para GitHub Container Registry
- ✅ Deploy para production (com aprovação)

## 🔍 Acompanhar Pipeline

```
Repository → Actions → CI/CD - Venda Distribuída
```

Você verá:
- 📊 Status de cada job
- ⏱️ Tempo de execução
- 📝 Logs detalhados
- 📦 Artifacts (se houver)

## 🐳 Usar Images no Seu Servidor

```bash
# Login
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Pull
docker pull ghcr.io/USERNAME/venda-distribuida-usuarios:latest

# Run
docker run -p 8080:8080 ghcr.io/USERNAME/venda-distribuida-usuarios:latest
```

## 🚀 Deploy Automático em Servidor

### Opção 1: Usar script fornecido

```bash
# No seu servidor:
curl -sSf https://raw.githubusercontent.com/USERNAME/venda-distribuida/main/deploy.sh | bash
```

### Opção 2: Docker Compose

```bash
# No seu servidor:
docker-compose -f docker-compose.prod.yml up -d
```

## 📧 Receber Notificações

```
Repository → Settings → Notifications → GitHub Actions
```

Configure para receber:
- ✉️ Falhas de workflow
- ✉️ Deploy completado
- ✉️ Reviews pendentes

## 🆘 Troubleshooting

### Testes falhando?
```
Actions → Último workflow → Clique no job com falha → Veja os logs
```

### Imagens não estão sendo pushadas?
```
1. Verifique se o GitHub Token tem permissão "write:packages"
2. Verifique se você está logado no ghcr.io
3. Veja os logs do job "Build"
```

### Deploy não funciona?
```
1. Verifique se o servidor tem docker-compose instalado
2. Verifique se as variáveis .env estão corretas
3. Veja os logs: docker-compose logs -f
```

## 📚 Documentação Completa

Para mais detalhes, veja: [CI-CD-README.md](CI-CD-README.md)

## 🎯 Próximos Passos

- [ ] Integrar com Kubernetes
- [ ] Adicionar testes E2E
- [ ] Configurar rollback automático
- [ ] Adicionar canary deployment
- [ ] Integrar com monitoring (Datadog, NewRelic)

---

**Quer adicionar mais workflows?** Veja exemplos em `.github/workflows/`
