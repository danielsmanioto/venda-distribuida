#!/bin/bash

# Script de inicialização do sistema de venda distribuída
# Execute: chmod +x start-all.sh && ./start-all.sh

set -e

echo "🚀 Iniciando Sistema de Venda Distribuída"
echo "=========================================="

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Função para verificar se um serviço está rodando
check_service() {
    local url=$1
    local name=$2
    
    echo -n "Verificando $name... "
    
    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ OK${NC}"
        return 0
    else
        echo -e "${RED}✗ FALHOU${NC}"
        return 1
    fi
}

# Verificar se Docker está rodando
echo ""
echo "📦 Verificando Docker..."
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker não está rodando. Por favor, inicie o Docker Desktop.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker está rodando${NC}"

# Subir infraestrutura
echo ""
echo "🐳 Subindo infraestrutura (Docker Compose)..."
docker-compose up -d

echo ""
echo "⏳ Aguardando infraestrutura inicializar (30 segundos)..."
sleep 30

# Verificar serviços de infraestrutura
echo ""
echo "🔍 Verificando serviços de infraestrutura..."

# PostgreSQL
docker exec postgres-usuarios pg_isready -U admin > /dev/null 2>&1 && echo -e "${GREEN}✓ PostgreSQL usuarios${NC}" || echo -e "${RED}✗ PostgreSQL usuarios${NC}"
docker exec postgres-produtos-master pg_isready -U admin > /dev/null 2>&1 && echo -e "${GREEN}✓ PostgreSQL produtos-master${NC}" || echo -e "${RED}✗ PostgreSQL produtos-master${NC}"
docker exec postgres-produtos-replica pg_isready -U admin > /dev/null 2>&1 && echo -e "${GREEN}✓ PostgreSQL produtos-replica${NC}" || echo -e "${RED}✗ PostgreSQL produtos-replica${NC}"
docker exec postgres-vendas pg_isready -U admin > /dev/null 2>&1 && echo -e "${GREEN}✓ PostgreSQL vendas${NC}" || echo -e "${RED}✗ PostgreSQL vendas${NC}"

# Redis
docker exec redis redis-cli -a redis123 PING > /dev/null 2>&1 && echo -e "${GREEN}✓ Redis${NC}" || echo -e "${RED}✗ Redis${NC}"

# RabbitMQ
curl -s -u admin:admin123 http://localhost:15672/api/overview > /dev/null 2>&1 && echo -e "${GREEN}✓ RabbitMQ${NC}" || echo -e "${RED}✗ RabbitMQ${NC}"

echo ""
echo "=========================================="
echo -e "${YELLOW}⚠️  ATENÇÃO: Execute os microserviços em terminais separados${NC}"
echo ""
echo "Terminal 1 - usuarios-service:"
echo "  cd usuarios && mvn spring-boot:run"
echo ""
echo "Terminal 2 - produtos-write-service:"
echo "  cd produtos-write-service && mvn spring-boot:run"
echo ""
echo "Terminal 3 - produtos-read-service:"
echo "  cd produtos-read-service && mvn spring-boot:run"
echo ""
echo "Terminal 4 - vendas-service:"
echo "  cd vendas && mvn spring-boot:run"
echo ""
echo "Terminal 5 - frontend:"
echo "  cd frontend && npm install && npm run dev"
echo ""
echo "=========================================="
echo ""
echo "📊 Acessos:"
echo "  Frontend:        http://localhost:5173"
echo "  usuarios:        http://localhost:8080"
echo "  produtos-write:  http://localhost:8081"
echo "  produtos-read:   http://localhost:8082"
echo "  vendas:          http://localhost:8083"
echo "  RabbitMQ UI:     http://localhost:15672 (admin/admin123)"
echo "  Grafana:         http://localhost:3000 (admin/admin123)"
echo "  Prometheus:      http://localhost:9090"
echo "  Jaeger:          http://localhost:16686"
echo ""
echo "✅ Infraestrutura pronta! Inicie os microserviços nos terminais."
