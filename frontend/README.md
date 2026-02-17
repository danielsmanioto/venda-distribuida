# Frontend - Venda Distribuída

Frontend simples para o sistema de venda distribuída. Interface para login de usuários e visualização do catálogo de produtos.

## 🚀 Tecnologias

- React 18
- Vite
- Axios
- CSS puro

## 📦 Funcionalidades

### ✅ Login
- Autenticação via JWT
- Integração com usuarios-service (porta 8080)
- Armazenamento de token em localStorage

### ✅ Catálogo de Produtos
- Listagem de todos os produtos
- Integração com produtos-read-service (porta 8082)
- Visualização de:
  - Nome do produto
  - Preço formatado em BRL
  - Estoque disponível
  - Categoria
  - SKU

## 🔧 Instalação

```bash
# Instalar dependências
npm install

# Executar em modo desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview
```

## 🌐 Configuração

### Porta
O frontend roda na porta **3000** por padrão.

### APIs Integradas
- **usuarios-service**: http://localhost:8080/api/auth/login
- **produtos-read-service**: http://localhost:8082/api/produtos

### Proxy
O Vite está configurado para fazer proxy de requisições `/api` para `http://localhost:8080` (usuarios-service).

## 📋 Estrutura de Pastas

```
frontend/
├── src/
│   ├── components/
│   │   ├── Login.jsx          # Tela de login
│   │   └── Produtos.jsx       # Listagem de produtos
│   ├── services/
│   │   └── api.js             # Configuração Axios e serviços
│   ├── App.jsx                # Componente principal
│   ├── main.jsx               # Entry point
│   └── index.css              # Estilos globais
├── index.html                 # HTML template
├── vite.config.js             # Configuração Vite
└── package.json
```

## 🎨 Interface

### Tela de Login
- Campo de email
- Campo de senha
- Validação de campos obrigatórios
- Mensagens de erro
- Loading state

### Tela de Produtos
- Grid responsivo de produtos
- Cards com informações do produto
- Botão de logout
- Estado de loading
- Estado vazio (sem produtos)
- Tratamento de erros

## 🔐 Autenticação

O sistema utiliza JWT (JSON Web Token):

1. Usuário faz login com email e senha
2. Backend retorna token JWT
3. Token é armazenado no localStorage
4. Token é enviado em todas as requisições via header `Authorization: Bearer <token>`
5. Logout remove o token do localStorage

## 🛡️ Tratamento de Erros

- Interceptor Axios para erros 401 (não autorizado)
- Redirecionamento automático para login em caso de sessão expirada
- Mensagens de erro amigáveis ao usuário

## 📱 Responsividade

- Grid de produtos adaptativo (minmax 250px)
- Layout responsivo
- Mobile-friendly

## 🎯 Como Usar

1. **Iniciar serviços backend**:
   ```bash
   # Subir infraestrutura (PostgreSQL, Redis, RabbitMQ, Kafka)
   docker-compose up -d

   # Iniciar usuarios-service (porta 8080)
   cd usuarios
   mvn spring-boot:run

   # Iniciar produtos-read-service (porta 8082)
   cd produtos-read-service
   mvn spring-boot:run
   ```

2. **Iniciar frontend**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. **Acessar aplicação**:
   - Abrir navegador em http://localhost:3000
   - Fazer login com credenciais cadastradas
   - Visualizar catálogo de produtos

## 🔄 Fluxo de Uso

1. Usuário acessa http://localhost:3000
2. Vê tela de login
3. Insere email e senha
4. Sistema autentica via usuarios-service
5. Recebe token JWT
6. É redirecionado para listagem de produtos
7. Sistema busca produtos via produtos-read-service
8. Exibe cards com informações dos produtos
9. Usuário pode fazer logout a qualquer momento

## 🌟 Melhorias Futuras

- [ ] Cadastro de usuários
- [ ] Filtros de produtos (categoria, preço)
- [ ] Busca de produtos
- [ ] Carrinho de compras
- [ ] Finalizar pedido (integração com vendas-service)
- [ ] Histórico de pedidos
- [ ] Paginação de produtos
- [ ] Integração com API Gateway
- [ ] Refresh token automático
- [ ] Toast notifications

## 📝 Observações

- Sem gateway: O frontend faz requisições diretas para os microserviços
- Produção: Em produção, recomenda-se usar um API Gateway
- CORS: Os backends devem ter CORS habilitado para permitir requisições do frontend
