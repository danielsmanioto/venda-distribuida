# 🚀 CI/CD Pipeline - Venda Distribuída

Pipeline completa de Integração Contínua e Deploy Contínuo para o sistema de venda distribuída.

## 📋 Workflows Implementados

### 1. CI/CD Principal (`ci-cd.yml`)

Pipeline completa que executa:

#### **Testes** (em paralelo)
- ✅ test-usuarios
- ✅ test-produtos-write
- ✅ test-produtos-read
- ✅ test-vendas
- ✅ test-frontend

#### **Build & Push** (após testes passarem)
- 🐳 Build de imagem Docker
- 📦 Push para GitHub Container Registry
- 🏷️ Tags automáticas (branch, SHA, latest)

#### **Deploy**
- 🧪 **Staging**: Deploy automático quando push em `develop`
- 🚀 **Production**: Deploy automático quando push em `main`

### 2. Security Scan (`security.yml`)

Verificações de segurança:
- 🔍 OWASP Dependency Check (vulnerabilidades em dependências)
- 🐳 Trivy (scan de containers)
- 🔐 Gitleaks (detecção de secrets)
- ⏰ Executa diariamente às 2h

### 3. Performance Tests (`performance.yml`)

Testes de carga com k6:
- 📊 Simula até 200 usuários simultâneos
- ⏱️ Verifica tempo de resposta (p95 < 500ms)
- 📉 Taxa de erro < 1%
- ⏰ Executa semanalmente

## 🔧 Configuração

### 1. Secrets do GitHub

Configure os seguintes secrets no GitHub:

```
Settings → Secrets and variables → Actions → New repository secret
```

**Obrigatórios:**
- `GITHUB_TOKEN` - Gerado automaticamente pelo GitHub

**Opcionais (para deploy real):**
- `DEPLOY_SSH_KEY` - Chave SSH para deploy em servidor
- `DEPLOY_HOST` - Host do servidor de produção
- `DEPLOY_USER` - Usuário SSH
- `DOCKERHUB_TOKEN` - Se usar Docker Hub ao invés de GHCR

### 2. Habilitar GitHub Container Registry

1. Vá em **Settings → Packages**
2. Configure visibilidade dos packages
3. O GITHUB_TOKEN já tem permissões necessárias

### 3. Configurar Ambientes

#### Staging
```
Settings → Environments → New environment → "staging"
```
- Não requer aprovação
- Deploy automático em `develop`

#### Production
```
Settings → Environments → New environment → "production"
```
- **Requer aprovação manual** (recomendado)
- Deploy automático em `main` após aprovação

## 📦 Docker Images

As imagens são publicadas em:
```
ghcr.io/SEU_USUARIO/venda-distribuida-usuarios:latest
ghcr.io/SEU_USUARIO/venda-distribuida-produtos-write:latest
ghcr.io/SEU_USUARIO/venda-distribuida-produtos-read:latest
ghcr.io/SEU_USUARIO/venda-distribuida-vendas:latest
ghcr.io/SEU_USUARIO/venda-distribuida-frontend:latest
```

### Tags disponíveis:
- `latest` - Última versão da branch main
- `develop` - Última versão da branch develop
- `main-abc123` - SHA específico da branch main
- `develop-abc123` - SHA específico da branch develop

## 🚀 Deploy em Produção

### Opção 1: Deploy Automático com Script

1. **No servidor de produção**, clone o repositório:
```bash
git clone https://github.com/SEU_USUARIO/venda-distribuida.git
cd venda-distribuida
```

2. **Configure o ambiente**:
```bash
cp .env.example .env
nano .env  # Edite com suas senhas
```

3. **Configure GitHub Token**:
```bash
# Crie um Personal Access Token no GitHub
# Settings → Developer settings → Personal access tokens → Generate new token
# Permissões: read:packages

export GITHUB_TOKEN=seu_token_aqui
```

4. **Execute o deploy**:
```bash
chmod +x deploy.sh
./deploy.sh
```

### Opção 2: Deploy Manual

```bash
# Login no GitHub Container Registry
echo $GITHUB_TOKEN | docker login ghcr.io -u SEU_USUARIO --password-stdin

# Pull das imagens
docker-compose -f docker-compose.prod.yml pull

# Subir serviços
docker-compose -f docker-compose.prod.yml up -d

# Verificar status
docker-compose -f docker-compose.prod.yml ps
```

### Opção 3: Deploy com SSH (GitHub Actions)

Adicione ao workflow `ci-cd.yml` no job de deploy:

```yaml
- name: Deploy via SSH
  uses: appleboy/ssh-action@master
  with:
    host: ${{ secrets.DEPLOY_HOST }}
    username: ${{ secrets.DEPLOY_USER }}
    key: ${{ secrets.DEPLOY_SSH_KEY }}
    script: |
      cd /app/venda-distribuida
      git pull origin main
      ./deploy.sh
```

## 🔄 Fluxo de Trabalho

### Development
```bash
# Criar feature branch
git checkout -b feature/nova-funcionalidade

# Fazer commits
git add .
git commit -m "feat: nova funcionalidade"

# Push para GitHub
git push origin feature/nova-funcionalidade

# Abrir Pull Request para develop
# → CI/CD executa testes
# → Após merge: deploy automático para staging
```

### Release
```bash
# Criar branch de release
git checkout -b release/v1.0.0 develop

# Ajustes finais
git commit -m "chore: bump version to 1.0.0"

# Merge para main
git checkout main
git merge release/v1.0.0
git tag v1.0.0
git push origin main --tags

# → CI/CD executa testes
# → Build de imagens
# → Deploy para production (com aprovação)
```

## 📊 Monitoramento da Pipeline

### GitHub Actions UI
```
Repository → Actions → CI/CD - Venda Distribuída
```

Você verá:
- ✅ Testes passando/falhando
- 🐳 Status do build de imagens
- 🚀 Status dos deploys
- ⏱️ Tempo de execução
- 📝 Logs detalhados

### Notificações

Configure notificações:
```
Settings → Notifications → GitHub Actions
```

## 🐛 Troubleshooting

### Build falha com "No space left on device"
```yaml
# Adicione ao job:
- name: Free disk space
  run: |
    sudo rm -rf /usr/share/dotnet
    sudo rm -rf /opt/ghc
    sudo rm -rf "/usr/local/share/boost"
    sudo rm -rf "$AGENT_TOOLSDIRECTORY"
```

### Timeout em testes
```yaml
# Aumente o timeout:
- name: Run tests
  timeout-minutes: 30
  run: mvn test
```

### Falha no push de imagens
```bash
# Verifique permissões do GITHUB_TOKEN
# Settings → Actions → General → Workflow permissions
# Selecione: "Read and write permissions"
```

### Deploy falha
```bash
# Verifique logs no servidor
docker-compose -f docker-compose.prod.yml logs -f usuarios-service

# Verifique variáveis de ambiente
docker-compose -f docker-compose.prod.yml config
```

## 📈 Métricas e KPIs

A pipeline coleta:

### Build Metrics
- ⏱️ Tempo total de build
- 🧪 Taxa de sucesso dos testes
- 📦 Tamanho das imagens Docker

### Deployment Metrics
- 🚀 Frequência de deploy
- ⏰ Lead time (commit → production)
- 🔄 Change failure rate
- 🔧 Mean time to recovery (MTTR)

### Quality Metrics
- ✅ Cobertura de testes
- 🔍 Vulnerabilidades encontradas
- 📊 Performance (p95, p99)

## 🔐 Segurança

### Best Practices Implementadas

1. **Secrets Management**
   - ✅ Nunca commitar secrets
   - ✅ Usar GitHub Secrets
   - ✅ Rotacionar credenciais regularmente

2. **Container Security**
   - ✅ Scan de vulnerabilidades (Trivy)
   - ✅ Base images oficiais
   - ✅ Multi-stage builds

3. **Dependency Security**
   - ✅ OWASP Dependency Check
   - ✅ Atualização automática (Dependabot)
   - ✅ Scan diário

4. **Code Security**
   - ✅ Secret scanning (Gitleaks)
   - ✅ SAST (Static Analysis)
   - ✅ Branch protection rules

## 🎯 Próximos Passos

- [ ] Integrar com Kubernetes (helm charts)
- [ ] Adicionar testes E2E
- [ ] Implementar blue-green deployment
- [ ] Adicionar canary releases
- [ ] Configurar rollback automático
- [ ] Integrar com Datadog/New Relic
- [ ] Adicionar smoke tests pós-deploy
- [ ] Implementar feature flags

## 📚 Referências

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [k6 Load Testing](https://k6.io/docs/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
