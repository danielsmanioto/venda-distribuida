package com.vendadistribuida.vendas.service;

import com.vendadistribuida.vendas.domain.dto.*;
import com.vendadistribuida.vendas.domain.entity.ItemVenda;
import com.vendadistribuida.vendas.domain.entity.Venda;
import com.vendadistribuida.vendas.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private VendaEventPublisher eventPublisher;

    @InjectMocks
    private VendaService vendaService;

    private VendaRequest vendaRequest;
    private Venda venda;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        // Setup ProdutoDTO
        produtoDTO = ProdutoDTO.builder()
                .id(1L)
                .nome("Notebook Dell")
                .preco(BigDecimal.valueOf(3500.00))
                .estoque(10)
                .ativo(true)
                .build();

        // Setup ItemVendaRequest
        ItemVendaRequest itemRequest = ItemVendaRequest.builder()
                .produtoId(1L)
                .quantidade(2)
                .build();

        // Setup VendaRequest
        vendaRequest = VendaRequest.builder()
                .usuarioId(1L)
                .itens(Arrays.asList(itemRequest))
                .build();

        // Setup ItemVenda
        ItemVenda itemVenda = ItemVenda.builder()
                .id(1L)
                .produtoId(1L)
                .produtoNome("Notebook Dell")
                .quantidade(2)
                .precoUnitario(BigDecimal.valueOf(3500.00))
                .build();

        // Setup Venda
        venda = Venda.builder()
                .id(1L)
                .usuarioId(1L)
                .status(Venda.StatusVenda.PENDENTE)
                .valorTotal(BigDecimal.valueOf(7000.00))
                .itens(Arrays.asList(itemVenda))
                .build();
    }

    @Test
    void deveCriarVendaComSucesso() {
        // Arrange
        when(produtoService.buscarProduto(1L)).thenReturn(produtoDTO);
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        doNothing().when(eventPublisher).publishVendaCriada(any());

        // Act
        VendaResponse response = vendaService.criarVenda(vendaRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUsuarioId());
        assertEquals(Venda.StatusVenda.PENDENTE, response.getStatus());
        assertTrue(response.getValorTotal().compareTo(BigDecimal.ZERO) > 0);
        verify(produtoService, times(1)).buscarProduto(1L);
        verify(vendaRepository, times(1)).save(any(Venda.class));
        verify(eventPublisher, times(1)).publishVendaCriada(any());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoInativo() {
        // Arrange
        produtoDTO.setAtivo(false);
        when(produtoService.buscarProduto(1L)).thenReturn(produtoDTO);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.criarVenda(vendaRequest);
        });

        assertTrue(exception.getMessage().contains("Produto inativo"));
        verify(produtoService, times(1)).buscarProduto(1L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        // Arrange
        produtoDTO.setEstoque(1);
        when(produtoService.buscarProduto(1L)).thenReturn(produtoDTO);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.criarVenda(vendaRequest);
        });

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
        verify(produtoService, times(1)).buscarProduto(1L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void deveBuscarVendaPorIdComSucesso() {
        // Arrange
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        // Act
        VendaResponse response = vendaService.buscarPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(vendaRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoVendaNaoEncontrada() {
        // Arrange
        when(vendaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.buscarPorId(1L);
        });

        assertTrue(exception.getMessage().contains("Venda não encontrada"));
        verify(vendaRepository, times(1)).findById(1L);
    }

    @Test
    void deveBuscarVendasPorUsuario() {
        // Arrange
        when(vendaRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(venda));

        // Act
        List<VendaResponse> responses = vendaService.buscarPorUsuario(1L);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getUsuarioId());
        verify(vendaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveListarVendasPaginadas() {
        // Arrange
        Page<Venda> page = new PageImpl<>(Arrays.asList(venda));
        when(vendaRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // Act
        Page<VendaResponse> responses = vendaService.listarPaginado(PageRequest.of(0, 20));

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        verify(vendaRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void deveCancelarVendaComSucesso() {
        // Arrange
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenReturn(venda);
        doNothing().when(eventPublisher).publishVendaCancelada(any());

        // Act
        VendaResponse response = vendaService.cancelarVenda(1L);

        // Assert
        assertNotNull(response);
        verify(vendaRepository, times(1)).findById(1L);
        verify(vendaRepository, times(1)).save(any(Venda.class));
        verify(eventPublisher, times(1)).publishVendaCancelada(any());
    }

    @Test
    void deveLancarExcecaoAoCancelarVendaJaCancelada() {
        // Arrange
        venda.setStatus(Venda.StatusVenda.CANCELADA);
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.cancelarVenda(1L);
        });

        assertTrue(exception.getMessage().contains("já cancelada"));
        verify(vendaRepository, times(1)).findById(1L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void deveLancarExcecaoAoCancelarVendaConfirmada() {
        // Arrange
        venda.setStatus(Venda.StatusVenda.CONFIRMADA);
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.cancelarVenda(1L);
        });

        assertTrue(exception.getMessage().contains("Não é possível cancelar venda confirmada"));
        verify(vendaRepository, times(1)).findById(1L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }
}
