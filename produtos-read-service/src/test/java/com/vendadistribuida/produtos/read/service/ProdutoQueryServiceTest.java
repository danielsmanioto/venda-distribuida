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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private Pageable pageable;

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
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        when(produtoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponse response = produtoQueryService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Notebook Dell", response.getNome());
        verify(produtoRepository, times(1)).findByIdAndAtivoTrue(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        when(produtoRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoQueryService.buscarPorId(1L);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository, times(1)).findByIdAndAtivoTrue(1L);
    }

    @Test
    void deveListarTodosProdutos() {
        // Arrange
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Mouse Logitech");
        produto2.setAtivo(true);

        when(produtoRepository.findByAtivoTrue()).thenReturn(Arrays.asList(produto, produto2));

        // Act
        List<ProdutoResponse> responses = produtoQueryService.listarTodos();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Notebook Dell", responses.get(0).getNome());
        assertEquals("Mouse Logitech", responses.get(1).getNome());
        verify(produtoRepository, times(1)).findByAtivoTrue();
    }

    @Test
    void deveBuscarPorCategoria() {
        // Arrange
        Page<Produto> page = new PageImpl<>(List.of(produto), pageable, 1);
        when(produtoRepository.findByCategoriaAndAtivoTrue("Eletrônicos", pageable))
            .thenReturn(page);

        // Act
        Page<ProdutoResponse> responses = produtoQueryService.buscarPorCategoria("Eletrônicos", pageable);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals("Eletrônicos", responses.getContent().get(0).getCategoria());
        verify(produtoRepository, times(1)).findByCategoriaAndAtivoTrue("Eletrônicos", pageable);
    }

    @Test
    void deveBuscarPorSku() {
        // Arrange
        when(produtoRepository.findBySku("DELL-001"))
                .thenReturn(Optional.of(produto));

        // Act
        ProdutoResponse response = produtoQueryService.buscarPorSku("DELL-001");

        // Assert
        assertNotNull(response);
        assertEquals("DELL-001", response.getSku());
        verify(produtoRepository, times(1)).findBySku("DELL-001");
    }

    @Test
    void deveBuscarPorTermo() {
        // Arrange
        Page<Produto> page = new PageImpl<>(List.of(produto), pageable, 1);
        when(produtoRepository.buscarPorTermo("dell", pageable))
            .thenReturn(page);

        // Act
        Page<ProdutoResponse> responses = produtoQueryService.buscar("dell", pageable);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertTrue(responses.getContent().get(0).getNome().toLowerCase().contains("dell"));
        verify(produtoRepository, times(1)).buscarPorTermo("dell", pageable);
    }
}
