# Processo CI/CD — Venda Distribuída

## Objetivo
Implementar uma esteira de CI/CD com GitHub Actions seguindo critérios de SRE e qualidade contínua.

## Status dos requisitos

- [x] Lint
- [x] Build
- [x] Unit tests (>=80%)
- [x] Integration tests
- [x] Sonar
- [x] SAST security
- [x] Dependency scan
- [x] Code review obrigatório
- [x] Coverage gate
- [x] Quality gate

## Implementação realizada

### 1) Lint
- Java com Checkstyle via `maven-checkstyle-plugin` em cada serviço.
- Frontend com ESLint (`eslint`, `eslint-plugin-react`, `eslint-plugin-react-hooks`).

Arquivos:
- `.github/workflows/ci-cd.yml`
- `checkstyle.xml`
- `frontend/.eslintrc.cjs`
- `frontend/package.json`

### 2) Build
- Build separado por estágio para Java (`mvn clean compile`) e frontend (`npm run build`).

Arquivo:
- `.github/workflows/ci-cd.yml`

### 3) Unit tests + cobertura >= 80%
- `jacoco-maven-plugin` configurado com regra mínima de cobertura de linha em `0.80`.
- Upload de cobertura para Codecov no workflow.

Arquivos:
- `.github/workflows/ci-cd.yml`
- `usuarios/pom.xml`
- `produtos-write-service/pom.xml`
- `produtos-read-service/pom.xml`
- `vendas/pom.xml`

### 4) Integration tests
- `maven-failsafe-plugin` configurado para executar `*IT.java`.
- Workflow com serviços de apoio (PostgreSQL, RabbitMQ, Redis e Kafka).

Arquivos:
- `.github/workflows/ci-cd.yml`
- `usuarios/pom.xml`
- `produtos-write-service/pom.xml`
- `produtos-read-service/pom.xml`
- `vendas/pom.xml`

### 5) Sonar + Quality Gate
- Execução do SonarCloud com `-Dsonar.qualitygate.wait=true` para bloquear pipeline em falha.
- `sonar-maven-plugin` adicionado nos serviços.

Arquivos:
- `.github/workflows/ci-cd.yml`
- `usuarios/pom.xml`
- `produtos-write-service/pom.xml`
- `produtos-read-service/pom.xml`
- `vendas/pom.xml`

### 6) SAST security
- CodeQL para Java/Kotlin e JavaScript/TypeScript.

Arquivo:
- `.github/workflows/security.yml`

### 7) Dependency scan
- OWASP Dependency-Check com upload SARIF.
- Arquivo de suppressions versionado.

Arquivos:
- `.github/workflows/security.yml`
- `owasp-suppressions.xml`

### 8) Code review obrigatório
- Arquivo CODEOWNERS criado.
- Template de PR com checklist obrigatório.

Arquivos:
- `.github/CODEOWNERS`
- `.github/PULL_REQUEST_TEMPLATE/pull_request_template.md`

### 9) Coverage gate
- Enforced no Maven via JaCoCo (`jacoco:check`).

Arquivos:
- `usuarios/pom.xml`
- `produtos-write-service/pom.xml`
- `produtos-read-service/pom.xml`
- `vendas/pom.xml`

### 10) Quality gate
- Enforced no SonarCloud via `sonar.qualitygate.wait=true`.

Arquivo:
- `.github/workflows/ci-cd.yml`

## Pré-requisitos no GitHub (Secrets/Variables)

### Secrets
- `SONAR_TOKEN`
- `CODECOV_TOKEN`

### Variables
- `SONAR_ORG`

## Branch protection (obrigatório)
Configurar nas branches `main` e `develop`:
1. Require pull request before merging
2. Require approvals (mínimo 1)
3. Require review from Code Owners
4. Require status checks to pass before merging

Status checks sugeridos:
- `🔍 Lint · ...`
- `🏗️ Build · ...`
- `🧪 Unit Tests · ...`
- `🔗 Integration Tests · ...`
- `📊 Sonar Quality Gate · ...`
- Jobs de `Security & SAST`

## Observações
- Serviços usam Java 21 (`usuarios`) e Java 25 (demais).
- Workflow já faz build/push de imagens Docker no GHCR em `push`.
- Deploy staging/production permanece com placeholders para comandos do ambiente alvo.
