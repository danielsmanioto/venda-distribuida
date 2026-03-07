# 🔄 Pipeline Visual

## Fluxo de CI/CD Completo

```
┌─────────────────────────────────────────────────────────────────┐
│                     DEVELOPER PUSH                              │
│                  git push origin feature                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  GitHub Actions │
                    │   Trigger Event │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
   ┌────▼────┐  ┌────────────▼──────────┐  ┌─────▼─────┐
   │ Lint &  │  │   Run Tests (All      │  │   Build   │
   │ Format  │  │   Services in         │  │   Check   │
   │         │  │   Parallel)           │  │           │
   │         │  │                       │  │           │
   │ ✓ PASS  │  │ • usuarios            │  │ ✓ PASS    │
   └────┬────┘  │ • produtos-write      │  └─────┬─────┘
        │       │ • produtos-read       │        │
        │       │ • vendas              │        │
        │       │ • frontend            │        │
        │       │                       │        │
        │       │ ✓ ALL PASS            │        │
        │       └──────────┬────────────┘        │
        │                  │                     │
        └──────────┬───────┴─────────────────────┘
                   │
            ┌──────▼──────────────────────────────┐
            │     BRANCH CHECK                    │
            │  Is it main or develop?             │
            └──────┬───────────────┬──────────────┘
                   │               │
            NO ╱───▼──────╲    YES ╱▼──────╲
              │  Merge on  │      │ Build │
              │   Feature  │      │Docker │
              │   PR OK ✓  │      │Images │
              └───────────┘      └──┬────┘
                   │               │
                   │         ┌─────▼──────────────────┐
                   │         │ Build Docker Image     │
                   │         │                        │
                   │         │ • usuarios             │
                   │         │ • produtos-write       │
                   │         │ • produtos-read        │
                   │         │ • vendas               │
                   │         │ • frontend             │
                   │         └──┬────────────┬────────┘
                   │            │            │
                   │       ┌─────▼────┐ ┌───▼─────┐
                   │       │   Push   │ │  Tag    │
                   │       │    to    │ │ Image:  │
                   │       │  GHCR    │ │         │
                   │       │          │ │ latest  │
                   │       │ ✓ Success│ │ branch  │
                   │       └─────┬────┘ │ sha     │
                   │             │      └────┬────┘
                   │             │           │
                   │       ┌─────▼───────────▼──┐
                   │       │  BRANCH ROUTING    │
                   │       └──┬──────────────┬──┘
                   │          │              │
         ┌─────────▼──┐    ┌──▼──────┐  ┌──▼────────┐
         │   DEVELOP  │    │ DEVELOP │  │   MAIN    │
         │            │    │ BRANCH  │  │  BRANCH   │
         └──┬─────────┘    │         │  │           │
            │              │  Deploy │  │  Deploy   │
            │              │  STAGING│  │ PRODUCTION│
            │              │ (auto)  │  │(approval) │
            │              └────┬────┘  └────┬──────┘
            │                   │            │
            │         ┌─────────▼────────────▼─┐
            │         │   Deploy Job           │
            │         │                        │
            │         │ 1. Pull images         │
            │         │ 2. Stop old services   │
            │         │ 3. Start new services  │
            │         │ 4. Run health checks   │
            │         │ 5. Notify on Slack     │
            │         └────────┬───────────────┘
            │                  │
            │         ┌────────▼────────────┐
            │         │ Health Check Pass?  │
            │         └────────┬──────┬─────┘
            │              YES │      │ NO
            │         ┌────────▼┐  ┌──▼────────┐
            │         │ ✅      │  │ 🔴 Alert  │
            │         │ SUCCESS │  │ FAILED    │
            │         └─────────┘  └───────────┘
            │                        │
            │                   ┌────▼─────────────┐
            └─────────────────▶ │ Daily Security   │
                                │ & Performance    │
                                │ Scans            │
                                └──────────────────┘
```

## Estado dos Serviços

```
┌─────────────────────────────────────────────────────────────┐
│                   GITHUB CONTAINER REGISTRY                 │
│  ghcr.io/username/venda-distribuida-*:latest/main/develop  │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  usuarios          ✅ main-a1b2c3d                          │
│  └─ Tag: latest                                             │
│  └─ Branch: main                                            │
│  └─ Size: 450 MB                                            │
│                                                              │
│  produtos-write    ✅ develop-x9y8z7w                       │
│  └─ Tag: latest                                             │
│  └─ Branch: develop                                         │
│  └─ Size: 520 MB                                            │
│                                                              │
│  produtos-read     ✅ main-p5q6r7s                          │
│  └─ Tag: latest                                             │
│  └─ Branch: main                                            │
│  └─ Size: 480 MB                                            │
│                                                              │
│  vendas            ✅ develop-m1n2o3p                       │
│  └─ Tag: latest                                             │
│  └─ Branch: develop                                         │
│  └─ Size: 510 MB                                            │
│                                                              │
│  frontend          ✅ main-f1g2h3i                          │
│  └─ Tag: latest                                             │
│  └─ Branch: main                                            │
│  └─ Size: 85 MB                                             │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## Matriz de Testes

```
┌──────────────────────────────────────────────────────────────┐
│                    TEST EXECUTION MATRIX                     │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  Pull Request/Push Event                                     │
│  ├─ Run on: ubuntu-latest                                   │
│  └─ Java version: 17 (Temurin)                              │
│                                                               │
│  Test Jobs (Parallel Execution)                              │
│  ├─ test-usuarios                                            │
│  │  ├─ Duration: ~3 min                                     │
│  │  ├─ Coverage: 85%                                        │
│  │  └─ Status: ✅ PASS                                      │
│  │                                                            │
│  ├─ test-produtos-write                                      │
│  │  ├─ Duration: ~2.5 min                                   │
│  │  ├─ Coverage: 78%                                        │
│  │  └─ Status: ✅ PASS                                      │
│  │                                                            │
│  ├─ test-produtos-read                                       │
│  │  ├─ Duration: ~2.8 min                                   │
│  │  ├─ Coverage: 81%                                        │
│  │  └─ Status: ✅ PASS                                      │
│  │                                                            │
│  ├─ test-vendas                                              │
│  │  ├─ Duration: ~2.2 min                                   │
│  │  ├─ Coverage: 76%                                        │
│  │  └─ Status: ✅ PASS                                      │
│  │                                                            │
│  └─ test-frontend                                            │
│     ├─ Duration: ~1.5 min                                   │
│     ├─ Coverage: 72%                                        │
│     └─ Status: ✅ PASS                                      │
│                                                               │
│  Total Time: ~3 min (parallel execution)                    │
│  Total Coverage: 78.4%                                      │
│  Build Artifacts: 2.1 GB                                    │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

## Deploy Timeline

```
Timeline: Feature → Production

T+0min ──▶ Developer: git push
           └─ GitHub Actions triggered

T+1min ──▶ Lint & Format Check
           └─ ✅ PASS

T+3min ──▶ All Tests Running
           ├─ usuarios test
           ├─ produtos-write test
           ├─ produtos-read test
           ├─ vendas test
           └─ frontend test
           └─ ✅ ALL PASS

T+6min ──▶ Build Docker Images
           ├─ Build usuarios
           ├─ Build produtos-write
           ├─ Build produtos-read
           ├─ Build vendas
           └─ Build frontend
           └─ ✅ ALL SUCCESS

T+12min ──▶ Push to Registry
           ├─ Push usuarios
           ├─ Push produtos-write
           ├─ Push produtos-read
           ├─ Push vendas
           └─ Push frontend
           └─ ✅ ALL PUSHED

T+15min ──▶ Deploy to Staging (if develop)
           ├─ Pull images
           ├─ Stop old services
           ├─ Start new services
           ├─ Run health checks
           └─ ✅ LIVE

T+15-20min ──▶ Manual Approval (if main)
              └─ Wait for approval...

T+25min ──▶ Deploy to Production
           ├─ Pull images
           ├─ Stop old services
           ├─ Start new services
           ├─ Run health checks
           └─ ✅ LIVE
```

## Security Scan Schedule

```
Every Day at 2:00 AM
├─ Dependency Check
│  ├─ usuarios
│  ├─ produtos-write
│  ├─ produtos-read
│  └─ vendas
│
├─ Container Scan (Trivy)
│  ├─ usuarios
│  ├─ produtos-write
│  ├─ produtos-read
│  ├─ vendas
│  └─ frontend
│
└─ Secret Scan (Gitleaks)
   └─ Repository
```

## Performance Test Schedule

```
Every Monday at 3:00 AM
├─ Load Test (k6)
│  ├─ Ramp up to 100 users (2 min)
│  ├─ Stay at 100 users (5 min)
│  ├─ Ramp up to 200 users (2 min)
│  ├─ Stay at 200 users (5 min)
│  └─ Ramp down to 0 users (2 min)
│
├─ Metrics Collected
│  ├─ HTTP request duration
│  ├─ Error rate
│  ├─ Success rate
│  └─ Response times (p50, p95, p99)
│
└─ Report Generated
   └─ Uploaded to artifacts
```

## Ambiente de Execução

```
┌──────────────────────────────────────────────────────────┐
│              GitHub Actions Runner (Linux)               │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  OS: ubuntu-latest (Ubuntu 20.04 LTS / 22.04 LTS)        │
│  CPU: 2-core                                             │
│  RAM: 7 GB                                               │
│  Storage: 14 GB SSD                                      │
│                                                           │
│  Pre-installed:                                          │
│  ├─ Docker                                               │
│  ├─ Docker Compose                                       │
│  ├─ Java 17 (Temurin)                                    │
│  ├─ Maven 3.8                                            │
│  ├─ Node.js 18                                           │
│  ├─ Git                                                  │
│  └─ GitHub CLI                                           │
│                                                           │
└──────────────────────────────────────────────────────────┘
```
