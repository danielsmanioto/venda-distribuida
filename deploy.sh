#!/bin/bash

# Script de deploy automático
# Este script pode ser usado em servidores para fazer deploy via SSH

set -e

echo "🚀 Deploy - Venda Distribuída"
echo "=============================="

# Verificar se .env existe
if [ ! -f .env ]; then
    echo "❌ Arquivo .env não encontrado!"
    echo "Copie .env.example para .env e configure as variáveis"
    exit 1
fi

# Carregar variáveis de ambiente
source .env

# Login no GitHub Container Registry
echo ""
echo "🔐 Fazendo login no GitHub Container Registry..."
echo $GITHUB_TOKEN | docker login ghcr.io -u $GITHUB_REPOSITORY_OWNER --password-stdin

# Parar serviços antigos
echo ""
echo "⏹️  Parando serviços antigos..."
docker-compose -f docker-compose.prod.yml down

# Fazer pull das novas imagens
echo ""
echo "📥 Baixando novas imagens..."
docker-compose -f docker-compose.prod.yml pull

# Subir serviços
echo ""
echo "▶️  Iniciando serviços..."
docker-compose -f docker-compose.prod.yml up -d

# Aguardar health checks
echo ""
echo "⏳ Aguardando serviços ficarem saudáveis..."
sleep 30

# Verificar status
echo ""
echo "📊 Status dos serviços:"
docker-compose -f docker-compose.prod.yml ps

# Verificar health checks
echo ""
echo "🏥 Verificando health checks..."

check_health() {
    local service=$1
    local url=$2
    
    if curl -f -s -o /dev/null "$url"; then
        echo "✅ $service - OK"
    else
        echo "❌ $service - FALHOU"
    fi
}

check_health "usuarios-service" "http://localhost:8080/actuator/health"
check_health "produtos-write-service" "http://localhost:8081/actuator/health"
check_health "produtos-read-service" "http://localhost:8082/actuator/health"
check_health "vendas-service" "http://localhost:8083/actuator/health"
check_health "frontend" "http://localhost:80"

echo ""
echo "✅ Deploy concluído!"
echo ""
echo "🌐 Acessos:"
echo "   Frontend:        http://localhost"
echo "   Prometheus:      http://localhost:9090"
echo "   Grafana:         http://localhost:3001"
echo "   Jaeger:          http://localhost:16686"
