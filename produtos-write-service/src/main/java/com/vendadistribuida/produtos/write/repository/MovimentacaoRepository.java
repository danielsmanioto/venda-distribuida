package com.vendadistribuida.produtos.write.repository;

import com.vendadistribuida.produtos.write.domain.entity.MovimentacaoEstoque;
import com.vendadistribuida.produtos.write.domain.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    Page<MovimentacaoEstoque> findByProdutoId(Long produtoId, Pageable pageable);

    Page<MovimentacaoEstoque> findByProdutoIdAndTipo(Long produtoId, TipoMovimentacao tipo, Pageable pageable);

    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.produtoId = :produtoId " +
           "AND m.criadoEm >= :dataInicio AND m.criadoEm <= :dataFim")
    Page<MovimentacaoEstoque> findByProdutoIdAndPeriodo(
            @Param("produtoId") Long produtoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.produtoId = :produtoId " +
           "AND m.tipo = :tipo AND m.criadoEm >= :dataInicio AND m.criadoEm <= :dataFim")
    Page<MovimentacaoEstoque> findByProdutoIdAndTipoAndPeriodo(
            @Param("produtoId") Long produtoId,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    long countByProdutoId(Long produtoId);
}
