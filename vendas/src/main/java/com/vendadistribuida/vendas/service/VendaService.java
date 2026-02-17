package com.vendadistribuida.vendas.service;

import com.vendadistribuida.vendas.domain.dto.*;
import com.vendadistribuida.vendas.domain.entity.ItemVenda;
import com.vendadistribuida.vendas.domain.entity.Venda;
import com.vendadistribuida.vendas.domain.event.VendaEvent;
import com.vendadistribuida.vendas.repository.VendaRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final VendaEventPublisher eventPublisher;

    @Transactional
    @CircuitBreaker(name = "vendas", fallbackMethod = "criarVendaFallback")
    @RateLimiter(name = "vendas")
    public VendaResponse criarVenda(VendaRequest request) {
        log.info("Criando nova venda para usuário: {}", request.getUsuarioId());

        // Criar venda
        Venda venda = Venda.builder()
                .usuarioId(request.getUsuarioId())
                .status(Venda.StatusVenda.PENDENTE)
                .build();

        // Adicionar itens e buscar dados dos produtos
        for (ItemVendaRequest itemRequest : request.getItens()) {
            ProdutoDTO produto = produtoService.buscarProduto(itemRequest.getProdutoId());

            if (!produto.getAtivo()) {
                throw new RuntimeException("Produto inativo: " + produto.getNome());
            }

            if (produto.getEstoque() < itemRequest.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para produto: " + produto.getNome());
            }

            ItemVenda item = ItemVenda.builder()
                    .produtoId(produto.getId())
                    .produtoNome(produto.getNome())
                    .quantidade(itemRequest.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build();

            venda.adicionarItem(item);
        }

        // Calcular valor total
        venda.calcularValorTotal();

        // Salvar venda
        Venda vendaSalva = vendaRepository.save(venda);
        log.info("Venda criada com sucesso: ID {}", vendaSalva.getId());

        // Publicar evento
        VendaEvent event = buildEvent(vendaSalva, VendaEvent.EventType.CRIADA);
        eventPublisher.publishVendaCriada(event);

        return mapToResponse(vendaSalva);
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "vendas")
    public VendaResponse buscarPorId(Long id) {
        log.info("Buscando venda por ID: {}", id);
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + id));
        return mapToResponse(venda);
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "vendas")
    public List<VendaResponse> buscarPorUsuario(Long usuarioId) {
        log.info("Buscando vendas do usuário: {}", usuarioId);
        return vendaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = "vendas")
    public Page<VendaResponse> listarPaginado(Pageable pageable) {
        log.info("Listando vendas com paginação");
        return vendaRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    @CircuitBreaker(name = "vendas")
    public VendaResponse cancelarVenda(Long id) {
        log.info("Cancelando venda: {}", id);

        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + id));

        if (venda.getStatus() == Venda.StatusVenda.CANCELADA) {
            throw new RuntimeException("Venda já cancelada");
        }

        if (venda.getStatus() == Venda.StatusVenda.CONFIRMADA) {
            throw new RuntimeException("Não é possível cancelar venda confirmada");
        }

        venda.setStatus(Venda.StatusVenda.CANCELADA);
        Venda vendaAtualizada = vendaRepository.save(venda);

        // Publicar evento
        VendaEvent event = buildEvent(vendaAtualizada, VendaEvent.EventType.CANCELADA);
        eventPublisher.publishVendaCancelada(event);

        log.info("Venda cancelada com sucesso: ID {}", id);
        return mapToResponse(vendaAtualizada);
    }

    private VendaResponse mapToResponse(Venda venda) {
        List<ItemVendaResponse> itensResponse = venda.getItens().stream()
                .map(item -> ItemVendaResponse.builder()
                        .id(item.getId())
                        .produtoId(item.getProdutoId())
                        .produtoNome(item.getProdutoNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return VendaResponse.builder()
                .id(venda.getId())
                .usuarioId(venda.getUsuarioId())
                .valorTotal(venda.getValorTotal())
                .status(venda.getStatus())
                .itens(itensResponse)
                .criadoEm(venda.getCriadoEm())
                .atualizadoEm(venda.getAtualizadoEm())
                .build();
    }

    private VendaEvent buildEvent(Venda venda, VendaEvent.EventType eventType) {
        List<VendaEvent.ItemVendaEvent> itensEvent = venda.getItens().stream()
                .map(item -> VendaEvent.ItemVendaEvent.builder()
                        .produtoId(item.getProdutoId())
                        .produtoNome(item.getProdutoNome())
                        .quantidade(item.getQuantidade())
                        .precoUnitario(item.getPrecoUnitario())
                        .build())
                .collect(Collectors.toList());

        return VendaEvent.builder()
                .vendaId(venda.getId())
                .usuarioId(venda.getUsuarioId())
                .valorTotal(venda.getValorTotal())
                .status(venda.getStatus().name())
                .itens(itensEvent)
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .build();
    }

    private VendaResponse criarVendaFallback(VendaRequest request, Exception ex) {
        log.error("Fallback criarVenda acionado: {}", ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.");
    }
}
