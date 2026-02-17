package com.vendadistribuida.produtos.read.repository;

import com.vendadistribuida.produtos.read.domain.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByIdAndAtivoTrue(Long id);

    Page<Produto> findByAtivoTrue(Pageable pageable);

    List<Produto> findByAtivoTrue();

    Page<Produto> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
            "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    Page<Produto> buscarPorTermo(@Param("termo") String termo, Pageable pageable);

    Optional<Produto> findBySku(String sku);
}
