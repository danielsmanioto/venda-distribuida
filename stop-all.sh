#!/bin/bash

# Script para parar todos os serviços
# Execute: chmod +x stop-all.sh && ./stop-all.sh

echo "🛑 Parando Sistema de Venda Distribuída"
echo "=========================================="

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

# Parar infraestrutura Docker
echo ""
echo "🐳 Parando containers Docker..."
docker-compose down

echo ""
echo -e "${GREEN}✓ Sistema parado com sucesso!${NC}"
echo ""
echo "⚠️  Para parar os microserviços, pressione Ctrl+C em cada terminal"
echo ""
echo "🗑️  Para remover volumes e dados:"
echo "   docker-compose down -v"
