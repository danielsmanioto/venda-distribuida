package com.vendadistribuida.produtos.write.service;

import com.vendadistribuida.produtos.write.domain.dto.ProdutoRequest;
import com.vendadistribuida.produtos.write.domain.dto.ProdutoResponse;
import com.vendadistribuida.produtos.write.domain.entity.Produto;
import com.vendadistribuida.produtos.write.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoEventPublisher eventPublisher;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequest produtoRequest;

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

        produtoRequest = new ProdutoRequest();
        produtoRequest.setNome("Notebook Dell");
        produtoRequest.setDescricao("Notebook Dell i7");
        produtoRequest.setPreco(BigDecimal.valueOf(3500.00));
        produtoRequest.setEstoque(10);
        produtoRequest.setCategoria("Eletrônicos");
        produtoRequest.setSku("DELL-001");
    }

    @Test
    void deveCriarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.existsBySku(anyString())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(eventPublisher).publicarProdutoCriado(any());

        // Act
        ProdutoResponse response = produtoService.criar(produtoRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Notebook Dell", response.getNome());
        assertEquals(BigDecimal.valueOf(3500.00), response.getPreco());
        verify(produtoRepository, times(1)).existsBySku("DELL-001");
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(eventPublisher, times(1)).publicarProdutoCriado(any());
    }

    @Test
    void deveLancarExcecaoQuandoSkuJaExiste() {
        // Arrange
        when(produtoRepository.existsBySku(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.criar(produtoRequest);
        });

        assertTrue(exception.getMessage().contains("SKU já cadastrado"));
        verify(produtoRepository, times(1)).existsBySku("DELL-001");
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(eventPublisher).publicarProdutoAtualizado(any());

        produtoRequest.setPreco(BigDecimal.valueOf(3200.00));

        // Act
        ProdutoResponse response = produtoService.atualizar(1L, produtoRequest);

        // Assert
        assertNotNull(response);
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(eventPublisher, times(1)).publicarProdutoAtualizado(any());
    }

    @Test
    void deveAtualizarEstoqueComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(eventPublisher).publicarProdutoAtualizado(any());

        // Act
        ProdutoResponse response = produtoService.atualizarEstoque(1L, 20);

        // Assert
        assertNotNull(response);
        assertEquals(20, response.getEstoque());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(eventPublisher, times(1)).publicarProdutoAtualizado(any());
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        doNothing().when(eventPublisher).publicarProdutoDeletado(any());

        // Act
        produtoService.deletar(1L);

        // Assert
        assertFalse(produto.getAtivo());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(produto);
        verify(eventPublisher, times(1)).publicarProdutoDeletado(any());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            produtoService.atualizar(1L, produtoRequest);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }
}
