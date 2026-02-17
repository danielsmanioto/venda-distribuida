import { useState, useEffect } from 'react';
import { produtosService, authService } from '../services/api';

function Produtos({ onLogout }) {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      setLoading(true);
      const data = await produtosService.listarTodos();
      setProdutos(data);
    } catch (err) {
      setError('Erro ao carregar produtos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  const formatarPreco = (preco) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(preco);
  };

  if (loading) {
    return (
      <div className="container">
        <div className="header">
          <h1>Produtos</h1>
          <button onClick={handleLogout}>Sair</button>
        </div>
        <div className="loading">Carregando produtos...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <div className="header">
          <h1>Produtos</h1>
          <button onClick={handleLogout}>Sair</button>
        </div>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="header">
        <h1>Catálogo de Produtos</h1>
        <button onClick={handleLogout}>Sair</button>
      </div>

      {produtos.length === 0 ? (
        <div className="empty-state">
          <h3>Nenhum produto disponível</h3>
          <p>Não há produtos cadastrados no momento.</p>
        </div>
      ) : (
        <div className="products-grid">
          {produtos.map((produto) => (
            <div key={produto.id} className="product-card">
              <h3>{produto.nome}</h3>
              <div className="price">{formatarPreco(produto.preco)}</div>
              <div className="stock">
                Estoque: {produto.estoque} unidades
              </div>
              {produto.categoria && (
                <span className="category">{produto.categoria}</span>
              )}
              {produto.sku && (
                <div style={{ fontSize: '12px', color: '#999', marginTop: '10px' }}>
                  SKU: {produto.sku}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Produtos;
