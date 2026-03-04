package com.vendadistribuida.produtos.write.service;

import com.vendadistribuida.produtos.write.domain.dto.*;
import com.vendadistribuida.produtos.write.domain.entity.MovimentacaoEstoque;
import com.vendadistribuida.produtos.write.domain.entity.Produto;
import com.vendadistribuida.produtos.write.domain.enums.TipoMovimentacao;
import com.vendadistribuida.produtos.write.exception.EstoqueInsuficienteException;
import com.vendadistribuida.produtos.write.exception.ProdutoNotFoundException;
import com.vendadistribuida.produtos.write.repository.MovimentacaoRepository;
import com.vendadistribuida.produtos.write.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public EntradaEstoqueResponse registrarEntrada(Long produtoId, MovimentacaoEstoqueRequest request) {
        log.info("Registrando entrada de estoque para produto {}: quantidade={}", produtoId, request.getQuantidade());

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        Integer saldoAnterior = produto.getEstoque() != null ? produto.getEstoque() : 0;
        Integer novoSaldo = saldoAnterior + request.getQuantidade();

        // Atualiza o saldo do produto
        produto.setEstoque(novoSaldo);
        produtoRepository.save(produto);

        // Registra a movimentação
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .produtoId(produtoId)
                .tipo(TipoMovimentacao.ENTRADA)
                .quantidade(request.getQuantidade())
                .motivo(request.getMotivo())
                .build();

        movimentacao = movimentacaoRepository.save(movimentacao);

        log.info("Entrada registrada com sucesso. Produto {}: saldo {} -> {}", 
                produtoId, saldoAnterior, novoSaldo);

        return EntradaEstoqueResponse.builder()
                .produtoId(produtoId)
                .saldoAnterior(saldoAnterior)
                .quantidadeAdicionada(request.getQuantidade())
                .saldoAtual(novoSaldo)
                .criadoEm(movimentacao.getCriadoEm())
                .build();
    }

    @Transactional
    public SaidaEstoqueResponse registrarSaida(Long produtoId, MovimentacaoEstoqueRequest request) {
        log.info("Registrando saída de estoque para produto {}: quantidade={}", produtoId, request.getQuantidade());

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        Integer saldoAnterior = produto.getEstoque() != null ? produto.getEstoque() : 0;

        // Valida se há estoque suficiente
        if (saldoAnterior < request.getQuantidade()) {
            log.warn("Estoque insuficiente para produto {}: saldo={}, solicitado={}", 
                    produtoId, saldoAnterior, request.getQuantidade());
            throw new EstoqueInsuficienteException(saldoAnterior, request.getQuantidade());
        }

        Integer novoSaldo = saldoAnterior - request.getQuantidade();

        // Atualiza o saldo do produto
        produto.setEstoque(novoSaldo);
        produtoRepository.save(produto);

        // Registra a movimentação
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .produtoId(produtoId)
                .tipo(TipoMovimentacao.SAIDA)
                .quantidade(request.getQuantidade())
                .motivo(request.getMotivo())
                .build();

        movimentacao = movimentacaoRepository.save(movimentacao);

        log.info("Saída registrada com sucesso. Produto {}: saldo {} -> {}", 
                produtoId, saldoAnterior, novoSaldo);

        return SaidaEstoqueResponse.builder()
                .produtoId(produtoId)
                .saldoAnterior(saldoAnterior)
                .quantidadeSaida(request.getQuantidade())
                .saldoAtual(novoSaldo)
                .criadoEm(movimentacao.getCriadoEm())
                .build();
    }

    @Transactional(readOnly = true)
    public SaldoEstoqueResponse consultarSaldo(Long produtoId) {
        log.info("Consultando saldo para produto {}", produtoId);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        Integer saldoAtual = produto.getEstoque() != null ? produto.getEstoque() : 0;

        return SaldoEstoqueResponse.builder()
                .produtoId(produtoId)
                .nomeProduto(produto.getNome())
                .saldoAtual(saldoAtual)
                .build();
    }

    @Transactional(readOnly = true)
    public HistoricoEstoqueResponse consultarHistorico(
            Long produtoId,
            TipoMovimentacao tipo,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer page,
            Integer size
    ) {
        log.info("Consultando histórico para produto {}: tipo={}, dataInicio={}, dataFim={}", 
                produtoId, tipo, dataInicio, dataFim);

        // Verifica se o produto existe
        if (!produtoRepository.existsById(produtoId)) {
            throw new ProdutoNotFoundException(produtoId);
        }

        // Configuração de paginação
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "criadoEm"));

        Page<MovimentacaoEstoque> movimentacoesPage;

        // Aplica filtros conforme parâmetros fornecidos
        if (tipo != null && dataInicio != null && dataFim != null) {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
            movimentacoesPage = movimentacaoRepository.findByProdutoIdAndTipoAndPeriodo(
                    produtoId, tipo, inicio, fim, pageable
            );
        } else if (tipo != null) {
            movimentacoesPage = movimentacaoRepository.findByProdutoIdAndTipo(produtoId, tipo, pageable);
        } else if (dataInicio != null && dataFim != null) {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
            movimentacoesPage = movimentacaoRepository.findByProdutoIdAndPeriodo(
                    produtoId, inicio, fim, pageable
            );
        } else {
            movimentacoesPage = movimentacaoRepository.findByProdutoId(produtoId, pageable);
        }

        // Converte para DTO
        List<MovimentacaoDto> movimentacoes = movimentacoesPage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        long totalMovimentacoes = movimentacaoRepository.countByProdutoId(produtoId);

        return HistoricoEstoqueResponse.builder()
                .produtoId(produtoId)
                .totalMovimentacoes(totalMovimentacoes)
                .page(pageNumber)
                .size(pageSize)
                .movimentacoes(movimentacoes)
                .build();
    }

    private MovimentacaoDto toDto(MovimentacaoEstoque movimentacao) {
        return MovimentacaoDto.builder()
                .id(movimentacao.getId())
                .tipo(movimentacao.getTipo())
                .quantidade(movimentacao.getQuantidade())
                .motivo(movimentacao.getMotivo())
                .criadoEm(movimentacao.getCriadoEm())
                .build();
    }
}
