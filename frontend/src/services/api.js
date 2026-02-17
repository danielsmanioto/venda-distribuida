import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (email, senha) => {
    const response = await api.post('/auth/login', { email, senha });
    const { token } = response.data;
    localStorage.setItem('token', token);
    return response.data;
  },
  
  logout: () => {
    localStorage.removeItem('token');
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
};

export const produtosService = {
  listarTodos: async () => {
    const response = await axios.get('http://localhost:8082/api/produtos');
    return response.data;
  },
  
  buscarPorCategoria: async (categoria) => {
    const response = await axios.get(`http://localhost:8082/api/produtos/categoria/${categoria}`);
    return response.data;
  },
};

export default api;
