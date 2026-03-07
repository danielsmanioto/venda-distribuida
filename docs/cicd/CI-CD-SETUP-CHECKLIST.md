# ✅ Checklist de Configuração - CI/CD

## 📋 Pre-requisitos

- [ ] Conta GitHub
- [ ] Repositório criado (este projeto)
- [ ] Acesso de administrador no repositório
- [ ] Terminal/CLI com Git configurado
- [ ] Docker Desktop instalado (local)

## 🔧 Passo 1: Configuração Inicial do GitHub

### Personal Access Token (PAT)

- [ ] Acesse: https://github.com/settings/tokens
- [ ] Clique em "Generate new token"
- [ ] Configure:
  - [ ] Name: `venda-distribuida-deploy`
  - [ ] Expiration: 90 dias (rotar regularmente)
  - [ ] Scopes:
    - [ ] `repo` (full control of private repositories)
    - [ ] `write:packages` (upload packages)
    - [ ] `read:packages` (download packages)
- [ ] Clique "Generate token"
- [ ] **COPIE O TOKEN** (será necessário no próximo passo)

### Secrets do Repositório

- [ ] Acesse: `Repository → Settings → Secrets and variables → Actions`
- [ ] Clique "New repository secret"
- [ ] Adicione:
  - [ ] Name: `GITHUB_TOKEN`
        Value: `<token copiado acima>`

## 🌍 Passo 2: Configurar Ambientes

### Staging Environment

- [ ] Acesse: `Repository → Settings → Environments`
- [ ] Clique "New environment"
- [ ] Name: `staging`
- [ ] Configurações:
  - [ ] Deployment branches: `develop`
  - [ ] Require approval: `false`
  - [ ] Allowed actions and reusable workflows: `All`

### Production Environment

- [ ] Acesse: `Repository → Settings → Environments`
- [ ] Clique "New environment"
- [ ] Name: `production`
- [ ] Configurações:
  - [ ] Deployment branches: `main`
  - [ ] Require approval: `true` ⭐ IMPORTANTE
  - [ ] Reviewers: Adicione co-owners
  - [ ] Allowed actions and reusable workflows: `All`

## 🛡️ Passo 3: Configurar Branch Protection

### Branch: main

- [ ] Acesse: `Repository → Settings → Branches`
- [ ] Clique "Add rule" ou edite regra existente
- [ ] Pattern: `main`
- [ ] Configurações:
  - [ ] Require pull request reviews before merging
    - [ ] Require code reviews: `2`
    - [ ] Dismiss stale pull request approvals when new commits are pushed
    - [ ] Require review from code owners
  - [ ] Require status checks to pass before merging
    - [ ] Require branches to be up to date before merging
    - [ ] Require passing status checks:
      - [ ] `test-usuarios`
      - [ ] `test-produtos-write`
      - [ ] `test-produtos-read`
      - [ ] `test-vendas`
      - [ ] `test-frontend`
  - [ ] Allow force pushes: `false`
  - [ ] Allow deletions: `false`

### Branch: develop

- [ ] Repita passos anteriores para `develop` com:
  - [ ] Require pull request reviews: `1`
  - [ ] Require branches to be up to date: `true`

## 👥 Passo 4: Configurar Times e Permissões

### Adicionar Colaboradores

- [ ] Acesse: `Repository → Settings → Collaborators and teams`
- [ ] Adicione colaboradores:
  - [ ] DevOps: `Admin` (pode mergear para main)
  - [ ] Developers: `Write` (podem mergear para develop)
  - [ ] QA: `Triage` (apenas leitura e feedback)

### Code Owners

- [ ] Crie arquivo: `.github/CODEOWNERS`
- [ ] Adicione:
  ```
  # Global owners
  * @seu-username
  
  # Services
  /usuarios/ @seu-username @outro-dev
  /produtos-write-service/ @seu-username @outro-dev
  /vendas/ @seu-username @devops
  /.github/workflows/ @seu-username @devops
  ```

## 📊 Passo 5: Configurar Webhooks (Opcional)

### Slack Integration

- [ ] Acesse: `Repository → Settings → Integrations`
- [ ] Procure por "Slack"
- [ ] Ou configure manualmente em: https://github.com/apps/slack
- [ ] Configure notificações para:
  - [ ] Workflow failures
  - [ ] Deployments
  - [ ] Security alerts

### Email Notifications

- [ ] Acesse: `Repository → Settings → Notifications`
- [ ] Configure:
  - [ ] Receive notifications: `Watching`
  - [ ] Email notifications: `Enabled`
  - [ ] Watch custom events:
    - [ ] Deployments
    - [ ] Actions failures

## 🔐 Passo 6: Configurar Secrets para Deploy (Opcional)

Se planeja fazer deploy automático via SSH:

- [ ] Gere par de chaves SSH:
  ```bash
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github-actions
  ```

- [ ] Adicione secrets:
  - [ ] Name: `DEPLOY_SSH_KEY`
        Value: `<conteúdo da chave privada>`
  - [ ] Name: `DEPLOY_HOST`
        Value: `seu-servidor.com`
  - [ ] Name: `DEPLOY_USER`
        Value: `deploy-user`

## 📝 Passo 7: Fazer Primeiro Push

- [ ] No seu terminal local:
  ```bash
  cd venda-distribuida
  git add .
  git commit -m "chore: setup CI/CD pipeline"
  git push origin main
  ```

- [ ] Acesse: `Repository → Actions`
- [ ] Você verá: `CI/CD - Venda Distribuída` executando

## ✨ Passo 8: Verificar Tudo Funcionando

### GitHub Actions UI

- [ ] Acesse: `Repository → Actions`
- [ ] Clique no workflow `CI/CD - Venda Distribuída`
- [ ] Verifique cada job:
  - [ ] ✅ test-usuarios
  - [ ] ✅ test-produtos-write
  - [ ] ✅ test-produtos-read
  - [ ] ✅ test-vendas
  - [ ] ✅ test-frontend

### Container Registry

- [ ] Acesse: `Repository → Packages`
- [ ] Verifique se as imagens foram publicadas:
  - [ ] venda-distribuida-usuarios
  - [ ] venda-distribuida-produtos-write
  - [ ] venda-distribuida-produtos-read
  - [ ] venda-distribuida-vendas
  - [ ] venda-distribuida-frontend

### Segurança

- [ ] Acesse: `Repository → Security → Code scanning`
- [ ] Verifique se há alertas (deve estar vazio no início)

## 📋 Passo 9: Configurar Workflows Adicionais

### Security Scan Agendado

- [ ] O arquivo `.github/workflows/security.yml` já existe
- [ ] Executa diariamente às 2 AM
- [ ] Nenhuma configuração adicional necessária

### Performance Tests

- [ ] O arquivo `.github/workflows/performance.yml` já existe
- [ ] Executa semanalmente (segunda 3 AM)
- [ ] Nenhuma configuração adicional necessária

## 🚀 Passo 10: Fazer Teste Completo

### Testar Feature Branch

```bash
# Criar feature branch
git checkout -b feature/test-pipeline

# Fazer change qualquer
echo "# Test" >> README.md

# Commit e push
git add .
git commit -m "test: pipeline test"
git push origin feature/test-pipeline

# Criar Pull Request
# → GitHub UI pedirá para criar PR
# → Todos os testes devem passar
# → Mergear para develop
```

### Testar Deploy para Staging

```bash
# Após merge para develop
# GitHub Actions automaticamente:
# 1. Executa testes
# 2. Faz build de imagens
# 3. Deploy para staging
# Monitorar em: Repository → Actions
```

### Testar Deploy para Production

```bash
# Criar release branch
git checkout -b release/v1.0.0 develop

# Bump version (exemplo)
echo "1.0.0" > VERSION

# Commit e push
git add .
git commit -m "chore: release v1.0.0"
git push origin release/v1.0.0

# Merge para main
git checkout main
git pull origin main
git merge release/v1.0.0
git tag v1.0.0
git push origin main --tags

# → GitHub Actions:
#   1. Executa testes
#   2. Faz build de imagens
#   3. AGUARDA APROVAÇÃO
# → Ir em: Repository → Actions → Deploy job
# → Clique "Review deployments"
# → Approve
# → Deployment em produção
```

## 🎯 Passo 11: Configuração de Monitoramento

### GitHub Insights

- [ ] Acesse: `Repository → Insights`
- [ ] Verifique:
  - [ ] Pulse (atividades recentes)
  - [ ] Code frequency
  - [ ] Network
  - [ ] Contributors

### Workflow Metrics

- [ ] Acesse: `Repository → Actions`
- [ ] Clique em workflow
- [ ] Verifique:
  - [ ] Success rate
  - [ ] Average run time
  - [ ] Successful runs

## 📧 Passo 12: Configurar Notificações

- [ ] GitHub Email:
  - [ ] `Repository → Settings → Notifications`
  - [ ] Configure preferências
  
- [ ] Slack (se instalado):
  - [ ] Você receberá notificações automaticamente
  
- [ ] Configuar custom webhooks:
  - [ ] `Repository → Settings → Webhooks`
  - [ ] Adicione endpoint customizado se necessário

## 🔄 Manutenção Contínua

### Semanal

- [ ] Revisar pull requests
- [ ] Verificar logs de segurança
- [ ] Verificar performance tests

### Mensal

- [ ] Atualizar dependências
- [ ] Revisar e renovar secrets/tokens
- [ ] Analisar custo de GitHub Actions

### Trimestral

- [ ] Rotacionar secrets/credentials
- [ ] Revisar policies de branch protection
- [ ] Atualizar documentação

## 🆘 Troubleshooting Rápido

| Problema | Solução |
|----------|---------|
| Testes não rodando | Verifique se `test-*.yml` estão em `.github/workflows/` |
| Imagens não pushing | Confirme GITHUB_TOKEN tem permissão `write:packages` |
| Deploy não aparece | Verifique se `docker-compose.prod.yml` existe e está correto |
| Branch protection bloqueando | Verifique se todos os testes estão passando |
| Workflow timeout | Aumente timeout no workflow YAML |

## ✅ Status Final

Quando tudo estiver configurado:

- [ ] Todos os secrets configurados ✅
- [ ] Ambientes criados ✅
- [ ] Branch protection ativada ✅
- [ ] Primeiro workflow executado com sucesso ✅
- [ ] Pull request testado ✅
- [ ] Deploy para staging funcionando ✅
- [ ] Deploy para production funcionando ✅
- [ ] Notificações configuradas ✅
- [ ] Documentação lida ✅

**🎉 CI/CD Pipeline 100% Configurada!**

---

### Próximos Passos

1. Fazer seu primeiro merge
2. Monitorar pipeline em tempo real
3. Ajustar workflows conforme necessário
4. Integrar mais ferramentas (SonarQube, Snyk, etc)
5. Documentar runbooks de deployment
