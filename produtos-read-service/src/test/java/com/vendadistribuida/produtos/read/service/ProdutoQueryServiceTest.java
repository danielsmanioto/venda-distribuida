package com.vendadistribuida.produtos.read.service;

import com.vendadistribuida.produtos.read.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.read.domain.entity.Produto;
import com.vendadistribuida.produtos.read.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoQueryServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoQueryService produtoQueryService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook Dell");
        produto.setDescricao("Notebook Dell i7");
        produto.setPreco(BigDecimal.valueOf(3500.00));
        produto.setEstoque(10);
        produto.setCategoria("Eletrônicos");
        produto.setSku("DELL-001");
        produto.setAtivo(true);
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponse response = produtoQueryService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Notebook Dell", response.getNome());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoQueryService.buscarPorId(1L);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarTodosProdutos() {
        // Arrange
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Mouse Logitech");
        produto2.setAtivo(true);

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto, produto2));

        // Act
        List<ProdutoResponse> responses = produtoQueryService.listarTodos();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Notebook Dell", responses.get(0).getNome());
        assertEquals("Mouse Logitech", responses.get(1).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarPorCategoria() {
        // Arrange
        when(produtoRepository.findByCategoriaAndAtivoTrue("Eletrônicos"))
                .thenReturn(Arrays.asList(produto));

        // Act
        List<ProdutoResponse> responses = produtoQueryService.buscarPorCategoria("Eletrônicos");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Eletrônicos", responses.get(0).getCategoria());
        verify(produtoRepository, times(1)).findByCategoriaAndAtivoTrue("Eletrônicos");
    }

    @Test
    void deveBuscarPorSku() {
        // Arrange
        when(produtoRepository.findBySkuAndAtivoTrue("DELL-001"))
                .thenReturn(Optional.of(produto));

        // Act
        ProdutoResponse response = produtoQueryService.buscarPorSku("DELL-001");

        // Assert
        assertNotNull(response);
        assertEquals("DELL-001", response.getSku());
        verify(produtoRepository, times(1)).findBySkuAndAtivoTrue("DELL-001");
    }

    @Test
    void deveBuscarPorTermo() {
        // Arrange
        when(produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue("dell"))
                .thenReturn(Arrays.asList(produto));

        // Act
        List<ProdutoResponse> responses = produtoQueryService.buscar("dell");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getNome().toLowerCase().contains("dell"));
        verify(produtoRepository, times(1)).findByNomeContainingIgnoreCaseAndAtivoTrue("dell");
    }
}
