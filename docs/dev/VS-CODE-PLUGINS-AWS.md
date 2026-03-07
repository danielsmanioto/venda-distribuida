# 📦 VS Code Plugins Essenciais para AWS, GitHub Copilot e Cloud Development

Guia completo dos plugins mais necessários para desenvolver e fazer deploy em AWS usando VS Code.

---

## 🎯 Plugins Obrigatórios

### 1️⃣ AWS Toolkit (ESSENCIAL)
**ID:** `amazonwebservices.aws-toolkit-vscode`

Integração completa com AWS direto no VS Code.

```bash
code --install-extension amazonwebservices.aws-toolkit-vscode
```

**Features principais:**
- ✅ Gerenciar Lambda functions
- ✅ Deploy de aplicações
- ✅ Explorer de recursos AWS (S3, DynamoDB, etc)
- ✅ CloudWatch Logs inline
- ✅ SAM (Serverless Application Model) support
- ✅ ECR (Elastic Container Registry) management
- ✅ Debugging remoto de Lambda

**Como usar:**
1. Clique em ícone AWS na barra lateral (ícone de caixa)
2. Fazer login com credenciais AWS
3. Selecione a região desejada
4. Explore recursos disponíveis

---

### 2️⃣ GitHub Copilot (IA para código)
**ID:** `GitHub.Copilot`

Assistente de IA que escreve código para você (como ChatGPT mas integrado no editor).

```bash
code --install-extension GitHub.Copilot
code --install-extension GitHub.Copilot-Chat
```

**Features:**
- ✅ Autocomplete inteligente em tempo real
- ✅ Gera funções completas baseado em comentário
- ✅ Explica código
- ✅ Refactoring automático
- ✅ Chat com IA integrado (`Ctrl+Shift+I`)

**Exemplo de uso:**
```python
# Digite um comentário:
# função para calcular fibonacci recursivo

# GitHub Copilot gera automaticamente:
def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)
```

---

### 3️⃣ Cloud Code (Google Cloud - mas funciona com AWS)
**ID:** `GoogleCloudTools.cloudcode`

Desenvolvimento integrado para cloud (suporta AWS via Terraform/CloudFormation).

```bash
code --install-extension GoogleCloudTools.cloudcode
```

**Features:**
- ✅ Debug remoto em containers
- ✅ Deploy automático
- ✅ Suporte a Kubernetes
- ✅ Templates de projetos cloud
- ✅ Integração com CI/CD

---

## 🏢 Plugins AWS Complementares

### 4️⃣ AWS Lambda
**ID:** `aws-scripting-guy.cdk-toolkit`

Específico para Lambda development.

```bash
code --install-extension aws-scripting-guy.cdk-toolkit
```

**Use quando:**
- Desenvolvimento de Lambda functions
- Suporte CDK (CloudFormation via Python/TypeScript)

---

### 5️⃣ AWS CloudFormation
**ID:** `aws-scripting-guy.aws-cloudformation-yaml`

Autocomplete e validação para CloudFormation templates.

```bash
code --install-extension aws-scripting-guy.aws-cloudformation-yaml
```

**Features:**
- ✅ Syntax highlighting para YAML/JSON
- ✅ Validação de templates
- ✅ Snippets prontos
- ✅ Integração com AWS Toolkit

---

### 6️⃣ Terraform (Infrastructure as Code)
**ID:** `HashiCorp.terraform`

Essencial se usar Terraform para provisionar AWS.

```bash
code --install-extension HashiCorp.terraform
```

**Features:**
- ✅ Syntax highlighting
- ✅ Autocomplete para recursos AWS
- ✅ Validação de módulos
- ✅ Integração com AWS Toolkit

---

## 🐳 Docker & Container Plugins

### 7️⃣ Docker (para ECR e container management)
**ID:** `ms-azuretools.vscode-docker`

Gerenciar Docker containers que vão para ECR.

```bash
code --install-extension ms-azuretools.vscode-docker
```

**Features:**
- ✅ Build e run containers
- ✅ Push para ECR
- ✅ Docker Compose support
- ✅ Linter para Dockerfile

---

### 8️⃣ Kubernetes (se usar EKS)
**ID:** `ms-kubernetes-tools.vscode-kubernetes-tools`

Desenvolvimento para EKS (Elastic Kubernetes Service).

```bash
code --install-extension ms-kubernetes-tools.vscode-kubernetes-tools
```

---

## 📊 Monitoramento & Logs

### 9️⃣ CloudWatch Logs Insights
**Incluído no AWS Toolkit** ✅

Já vem integrado! Veja logs em tempo real.

**Como usar:**
1. AWS Toolkit → CloudWatch Logs
2. Selecione Log Group
3. Veja logs inline no editor

---

### 🔟 Log Parser
**ID:** `bernaferrari.log-parser`

Parse e filtro de logs com facilidade.

```bash
code --install-extension bernaferrari.log-parser
```

---

## 🔐 Segurança & Credentials

### 1️⃣1️⃣ AWS IAM (Credentials Management)
**ID:** `AmazonWebservices.Amazon-Q-VSCode`

Gerenciar credenciais AWS com segurança.

```bash
code --install-extension AmazonWebservices.Amazon-Q-VSCode
```

**Features:**
- ✅ Armazenar credenciais seguramente
- ✅ Suporte MFA
- ✅ Role switching rápido

---

### 1️⃣2️⃣ Secrets Manager
**ID:** `ms-vscode.vscode-credentials-provider`

Gerenciar secrets do AWS Secrets Manager.

```bash
code --install-extension ms-vscode.vscode-credentials-provider
```

---

## 💻 Desenvolvimento & Linguagens

### 1️⃣3️⃣ Python Extension Pack (para Lambda)
**ID:** `ms-python.python`

Essencial se desenvolver Lambda em Python.

```bash
code --install-extension ms-python.python
code --install-extension ms-python.vscode-pylance
code --install-extension ms-python.debugpy
```

---

### 1️⃣4️⃣ Java Extension Pack (já instalado!)
**ID:** `vscjava.vscode-java-pack`

Para Lambda em Java ou Spring Boot no AWS.

```bash
code --install-extension vscjava.vscode-java-pack
```

---

### 1️⃣5️⃣ Node.js & TypeScript
**ID:** `ms-vscode.js-debug`

Para Lambda em Node.js/TypeScript.

```bash
code --install-extension ms-vscode.js-debug-nightly
```

---

## 📝 Outros Úteis

### 1️⃣6️⃣ REST Client (testar APIs)
**ID:** `humao.rest-client`

Testar endpoints API sem sair do VS Code.

```bash
code --install-extension humao.rest-client
```

**Exemplo:**
```http
### Chamar Lambda via API Gateway
POST https://seu-api-gateway.amazonaws.com/usuarios
Authorization: Bearer seu_token_jwt

{
  "nome": "João",
  "email": "joao@example.com"
}
```

---

### 1️⃣7️⃣ Thunder Client (alternativa ao Postman)
**ID:** `rangav.vscode-thunder-client`

Cliente HTTP com UI melhor que REST Client.

```bash
code --install-extension rangav.vscode-thunder-client
```

---

### 1️⃣8️⃣ GitLens (Git History)
**ID:** `eamodio.gitlens`

Ver histórico de commits e quem mexeu em cada linha.

```bash
code --install-extension eamodio.gitlens
```

---

### 1️⃣9️⃣ GitHub Pull Requests
**ID:** `GitHub.vscode-pull-request-github`

Gerenciar PRs sem sair do VS Code.

```bash
code --install-extension GitHub.vscode-pull-request-github
```

---

### 2️⃣0️⃣ Settings Sync (sincronizar config)
**ID:** `Shan.code-settings-sync`

Sincronizar plugins e configs entre computadores.

```bash
code --install-extension Shan.code-settings-sync
```

---

## 📋 Kit de Instalação Rápida

### Script para instalar TUDO de uma vez:

```bash
#!/bin/bash
# save as: install-aws-plugins.sh

echo "📦 Instalando plugins essenciais para AWS..."

# Essenciais
code --install-extension amazonwebservices.aws-toolkit-vscode
code --install-extension GitHub.Copilot
code --install-extension GitHub.Copilot-Chat
code --install-extension GoogleCloudTools.cloudcode

# AWS específicos
code --install-extension aws-scripting-guy.cdk-toolkit
code --install-extension aws-scripting-guy.aws-cloudformation-yaml
code --install-extension HashiCorp.terraform

# Docker & Kubernetes
code --install-extension ms-azuretools.vscode-docker
code --install-extension ms-kubernetes-tools.vscode-kubernetes-tools

# Python/Node/Java
code --install-extension ms-python.python
code --install-extension ms-python.vscode-pylance
code --install-extension vscjava.vscode-java-pack
code --install-extension ms-vscode.js-debug-nightly

# Utilitários
code --install-extension humao.rest-client
code --install-extension rangav.vscode-thunder-client
code --install-extension eamodio.gitlens
code --install-extension GitHub.vscode-pull-request-github
code --install-extension Shan.code-settings-sync
code --install-extension bernaferrari.log-parser

echo "✅ Todos os plugins instalados! Reinicie o VS Code."
```

**Como usar:**
```bash
chmod +x install-aws-plugins.sh
./install-aws-plugins.sh
```

---

## ⚙️ Configuração Pós-Instalação

### 1. Configurar AWS Credentials

```bash
# Via AWS CLI
aws configure

# Ou via AWS Toolkit no VS Code:
# 1. Clique no ícone AWS
# 2. "Add New Connection"
# 3. Selecione "AWS IAM Credentials"
# 4. Cole Access Key + Secret Key
```

### 2. Adicionar no `.vscode/settings.json`:

```json
{
  // AWS Toolkit
  "aws.profile": "default",
  "aws.region": "us-east-1",
  "aws.samcli.location": "sam",
  
  // Copilot
  "github.copilot.enable": {
    "*": true,
    "plaintext": false,
    "markdown": false
  },
  
  // Cloud Code
  "cloudcode.minikubeProfile": "default",
  
  // Python
  "python.linting.enabled": true,
  "python.linting.pylintEnabled": true,
  
  // Terraform
  "terraform.path": "terraform",
  "terraform.lintPath": "tflint",
  
  // Docker
  "docker.showPerformanceHints": false
}
```

---

## 🎓 Exemplos Práticos

### Deploy de Lambda com AWS Toolkit

**Passo 1:** Criar função
```bash
# Via CLI
sam init

# Selecione template Python/Node/Java
```

**Passo 2:** No VS Code
1. Abra a pasta do projeto
2. AWS Toolkit → Template Explorer
3. Escolha template SAM
4. Clique em "Deploy"

**Passo 3:** Configurar

```bash
# Deploy via CLI (alternativa)
sam deploy --guided
```

---

### Usar Copilot para gerar Lambda Handler

```python
# Comente isso:
# Lambda handler que recebe um evento com "nome" e retorna "Olá {nome}"

# GitHub Copilot gera automaticamente:
def lambda_handler(event, context):
    nome = event.get('nome', 'Mundo')
    return {
        'statusCode': 200,
        'body': f'Olá {nome}'
    }
```

---

### Testar API via REST Client

Crie arquivo `test-api.http`:

```http
### Variáveis
@baseUrl = https://seu-api-gateway.amazonaws.com
@token = seu_jwt_token

### Criar usuário
POST {{baseUrl}}/usuarios
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "nome": "João Silva",
  "email": "joao@example.com"
}

### Listar usuários
GET {{baseUrl}}/usuarios
Authorization: Bearer {{token}}

### Deletar usuário
DELETE {{baseUrl}}/usuarios/1
Authorization: Bearer {{token}}
```

Clique em "Send Request" para testar!

---

## 📊 Resumo de Plugins por Categoria

### 🔴 CRÍTICOS (instale primeiro)
| Plugin | Uso |
|--------|-----|
| AWS Toolkit | Gerenciar recursos AWS |
| GitHub Copilot | IA para código |
| Cloud Code | Deploy em cloud |

### 🟠 MUITO IMPORTANTES
| Plugin | Uso |
|--------|-----|
| Docker | Containers para ECR |
| Python / Java / Node | Linguagens para Lambda |
| REST Client | Testar APIs |
| CloudFormation | Infrastructure as Code |
| Terraform | Provisionar recursos |

### 🟡 RECOMENDADOS
| Plugin | Uso |
|--------|-----|
| GitLens | Git history |
| Kubernetes | Se usar EKS |
| Thunder Client | Alternativa REST Client |
| GitHub PRs | Gerenciar pull requests |
| Log Parser | Analisar logs |

### 🟢 OPCIONAIS
| Plugin | Uso |
|--------|-----|
| Settings Sync | Sincronizar config |
| Prettier | Formatar código |
| ESLint | Linter JavaScript |

---

## 🆚 Comparação: VS Code vs IDE Online AWS

| Feature | VS Code | AWS IDE Online |
|---------|---------|----------------|
| **Speed** | ⚡ Muito rápido | 🐢 Lento (browser) |
| **Offline** | ✅ Funciona | ❌ Precisa internet |
| **Plugins** | ✅ Ecossistema grande | ❌ Limitado |
| **RAM** | 💾 500MB | 💾 1GB+ (browser) |
| **Customize** | ✅ Total | ❌ Limitado |
| **Free** | 🆓 Sim | 🆓 Sim (AWS free tier) |
| **Cloud Sync** | ✅ Opcional | ✅ Automático |

**Recomendação:** Use VS Code local + Cloud Code para deploy.

---

## 🚀 Workflow Completo

### 1️⃣ Desenvolvimento Local
```bash
# Criar projeto
sam init
cd meu-projeto

# Editar código no VS Code
code .

# GitHub Copilot escreve o handler para você
# Você aprova/edita conforme necessário
```

### 2️⃣ Testar Localmente
```bash
# No terminal VS Code
sam build
sam local start-api

# Testar via REST Client
# GET http://localhost:3000/usuarios
```

### 3️⃣ Deploy para AWS
```bash
# Via AWS Toolkit
# Clique em "Deploy SAM Application"
# Ou via CLI:
sam deploy --guided
```

### 4️⃣ Monitorar
```bash
# AWS Toolkit → CloudWatch Logs
# Ver logs em tempo real no VS Code
```

---

## 🆘 Troubleshooting

### Copilot não funciona
```bash
# Verificar login
# Command Palette (Cmd+Shift+P)
# "GitHub Copilot: Sign In"
```

### AWS Toolkit não conecta
```bash
# Verificar credenciais
aws sts get-caller-identity

# Se erro, refaça setup:
# AWS Toolkit → "Add New Connection"
```

### Docker não encontra ECR
```bash
# Verificar AWS region
aws configure get region

# Adicionar no settings.json:
"aws.region": "us-east-1"
```

---

## 📚 Documentação Relacionada

- [INFRAESTRUTURA.md](../guias/INFRAESTRUTURA.md) - Guia de acesso aos bancos/serviços
- [README.md](README.md) - Visão geral do projeto
- [AWS Toolkit Docs](https://docs.aws.amazon.com/toolkit-for-vscode/)
- [GitHub Copilot Docs](https://github.com/features/copilot)
- [Cloud Code Docs](https://cloud.google.com/code/docs)

---

## 📞 Quick Start

**5 minutos para estar pronto:**

```bash
# 1. Instalar plugins
code --install-extension amazonwebservices.aws-toolkit-vscode
code --install-extension GitHub.Copilot
code --install-extension GoogleCloudTools.cloudcode

# 2. Fazer login AWS
aws configure

# 3. Reiniciar VS Code
# (Ctrl+Shift+P → "Reload Window")

# 4. Começar a desenvolver!
# AWS Toolkit vai aparecer na barra lateral
```

**Pronto! Agora você tem VS Code + AWS + IA para código! 🚀**

---

<div align="center">

**Feito para desenvolvimento rápido e eficiente na AWS**

AWS + GitHub Copilot + VS Code = 🚀 Super Produtividade

</div>
