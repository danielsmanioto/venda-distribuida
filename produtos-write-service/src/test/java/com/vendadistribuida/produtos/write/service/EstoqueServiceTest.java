package com.vendadistribuida.produtos.write.service;

import com.vendadistribuida.produtos.write.domain.dto.*;
import com.vendadistribuida.produtos.write.domain.entity.MovimentacaoEstoque;
import com.vendadistribuida.produtos.write.domain.entity.Produto;
import com.vendadistribuida.produtos.write.domain.enums.TipoMovimentacao;
import com.vendadistribuida.produtos.write.exception.EstoqueInsuficienteException;
import com.vendadistribuida.produtos.write.exception.ProdutoNotFoundException;
import com.vendadistribuida.produtos.write.repository.MovimentacaoRepository;
import com.vendadistribuida.produtos.write.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Produto produto;
    private MovimentacaoEstoqueRequest request;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(1L)
                .nome("Produto Teste")
                .estoque(10)
                .build();

        request = MovimentacaoEstoqueRequest.builder()
                .quantidade(5)
                .motivo("Teste")
                .build();
    }

    @Test
    @DisplayName("Deve registrar entrada e incrementar saldo corretamente")
    void deveRegistrarEntradaComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .id(1L)
                .produtoId(1L)
                .tipo(TipoMovimentacao.ENTRADA)
                .quantidade(5)
                .motivo("Teste")
                .criadoEm(LocalDateTime.now())
                .build();
        
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenReturn(movimentacao);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        EntradaEstoqueResponse response = estoqueService.registrarEntrada(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getProdutoId());
        assertEquals(10, response.getSaldoAnterior());
        assertEquals(5, response.getQuantidadeAdicionada());
        assertEquals(15, response.getSaldoAtual());
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(movimentacaoRepository, times(1)).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar entrada em produto inexistente")
    void deveLancarExcecaoAoRegistrarEntradaEmProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class, () -> {
            estoqueService.registrarEntrada(99L, request);
        });

        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve registrar saída e decrementar saldo corretamente")
    void deveRegistrarSaidaComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .id(1L)
                .produtoId(1L)
                .tipo(TipoMovimentacao.SAIDA)
                .quantidade(5)
                .motivo("Teste")
                .criadoEm(LocalDateTime.now())
                .build();
        
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenReturn(movimentacao);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        SaidaEstoqueResponse response = estoqueService.registrarSaida(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getProdutoId());
        assertEquals(10, response.getSaldoAnterior());
        assertEquals(5, response.getQuantidadeSaida());
        assertEquals(5, response.getSaldoAtual());
        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(movimentacaoRepository, times(1)).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar saída com estoque insuficiente")
    void deveLancarExcecaoAoRegistrarSaidaComEstoqueInsuficiente() {
        // Arrange
        produto.setEstoque(3);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        MovimentacaoEstoqueRequest requestMaior = MovimentacaoEstoqueRequest.builder()
                .quantidade(5)
                .motivo("Teste")
                .build();

        // Act & Assert
        EstoqueInsuficienteException exception = assertThrows(EstoqueInsuficienteException.class, () -> {
            estoqueService.registrarSaida(1L, requestMaior);
        });

        assertEquals(3, exception.getSaldoAtual());
        assertEquals(5, exception.getQuantidadeSolicitada());
        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar saída em produto inexistente")
    void deveLancarExcecaoAoRegistrarSaidaEmProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class, () -> {
            estoqueService.registrarSaida(99L, request);
        });

        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve consultar saldo corretamente")
    void deveConsultarSaldoComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        SaldoEstoqueResponse response = estoqueService.consultarSaldo(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getProdutoId());
        assertEquals("Produto Teste", response.getNomeProduto());
        assertEquals(10, response.getSaldoAtual());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar saldo de produto inexistente")
    void deveLancarExcecaoAoConsultarSaldoDeProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class, () -> {
            estoqueService.consultarSaldo(99L);
        });
    }

    @Test
    @DisplayName("Deve retornar histórico paginado corretamente")
    void deveRetornarHistoricoPaginadoComSucesso() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);

        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .id(1L)
                .produtoId(1L)
                .tipo(TipoMovimentacao.ENTRADA)
                .quantidade(5)
                .motivo("Teste")
                .criadoEm(LocalDateTime.now())
                .build();

        Page<MovimentacaoEstoque> page = new PageImpl<>(Collections.singletonList(movimentacao));
        when(movimentacaoRepository.findByProdutoId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(movimentacaoRepository.countByProdutoId(1L)).thenReturn(1L);

        // Act
        HistoricoEstoqueResponse response = estoqueService.consultarHistorico(1L, null, null, null, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getProdutoId());
        assertEquals(1L, response.getTotalMovimentacoes());
        assertEquals(0, response.getPage());
        assertEquals(20, response.getSize());
        assertEquals(1, response.getMovimentacoes().size());
    }

    @Test
    @DisplayName("Deve filtrar histórico por tipo")
    void deveFiltrarHistoricoPorTipo() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);

        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .id(1L)
                .produtoId(1L)
                .tipo(TipoMovimentacao.SAIDA)
                .quantidade(5)
                .motivo("Venda")
                .criadoEm(LocalDateTime.now())
                .build();

        Page<MovimentacaoEstoque> page = new PageImpl<>(Collections.singletonList(movimentacao));
        when(movimentacaoRepository.findByProdutoIdAndTipo(eq(1L), eq(TipoMovimentacao.SAIDA), any(Pageable.class)))
                .thenReturn(page);
        when(movimentacaoRepository.countByProdutoId(1L)).thenReturn(10L);

        // Act
        HistoricoEstoqueResponse response = estoqueService.consultarHistorico(
                1L, TipoMovimentacao.SAIDA, null, null, 0, 10
        );

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getMovimentacoes().size());
        assertEquals(TipoMovimentacao.SAIDA, response.getMovimentacoes().get(0).getTipo());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para produto sem movimentações")
    void deveRetornarListaVaziaParaProdutoSemMovimentacoes() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);

        Page<MovimentacaoEstoque> page = new PageImpl<>(Collections.emptyList());
        when(movimentacaoRepository.findByProdutoId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(movimentacaoRepository.countByProdutoId(1L)).thenReturn(0L);

        // Act
        HistoricoEstoqueResponse response = estoqueService.consultarHistorico(1L, null, null, null, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalMovimentacoes());
        assertTrue(response.getMovimentacoes().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar histórico de produto inexistente")
    void deveLancarExcecaoAoConsultarHistoricoDeProdutoInexistente() {
        // Arrange
        when(produtoRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class, () -> {
            estoqueService.consultarHistorico(99L, null, null, null, 0, 20);
        });
    }
}
