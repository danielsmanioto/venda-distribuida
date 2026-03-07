# 🖥️ Frontend - Venda Distribuída

<p align="center">
  <img src="https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" />
  <img src="https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite" />
  <img src="https://img.shields.io/badge/Axios-HTTP-5A29E4?style=for-the-badge" alt="Axios" />
</p>

Interface web para login e catálogo de produtos da plataforma.

---

## 📌 Índice

- [✨ Funcionalidades](#-funcionalidades)
- [🧩 Stack](#-stack)
- [⚙️ Configuração](#️-configuração)
- [🚀 Executando localmente](#-executando-localmente)
- [📁 Estrutura](#-estrutura)
- [🔐 Autenticação](#-autenticação)

---

## ✨ Funcionalidades

- Login com JWT
- Listagem de produtos
- Logout e expiração de sessão
- Tratamento automático para `401`
- Layout responsivo

---

## 🧩 Stack

- React 18
- Vite 5
- Axios
- CSS

---

## ⚙️ Configuração

### Portas

- Frontend (dev): `3000`
- Usuários API: `8080`
- Produtos Read API: `8082`

### Integrações

- Login: `POST /api/auth/login` (proxy para usuários)
- Produtos: `GET http://localhost:8082/api/produtos`

---

## 🚀 Executando localmente

```bash
npm install
npm run dev
```

Build de produção:

```bash
npm run build
npm run preview
```

---

## 📁 Estrutura

```text
frontend/
├── src/
│   ├── components/
│   │   ├── Login.jsx
│   │   └── Produtos.jsx
│   ├── services/
│   │   └── api.js
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── index.html
├── vite.config.js
└── package.json
```

---

## 🔐 Autenticação

Fluxo:

1. Usuário faz login com email/senha
2. API retorna JWT
3. Token salvo em `localStorage`
4. Token enviado no header `Authorization: Bearer <token>`
5. Em `401`, sessão é encerrada e login é solicitado novamente

---

## 🔭 Próximos passos

- Filtros e busca avançada de produtos
- Carrinho de compras
- Fluxo de checkout integrado ao serviço de vendas
- Melhorias visuais e notificações
