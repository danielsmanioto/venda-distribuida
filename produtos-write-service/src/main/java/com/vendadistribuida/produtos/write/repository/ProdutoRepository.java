package com.vendadistribuida.produtos.write.repository;

import com.vendadistribuida.produtos.write.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findBySku(String sku);

    Boolean existsBySku(String sku);
}
