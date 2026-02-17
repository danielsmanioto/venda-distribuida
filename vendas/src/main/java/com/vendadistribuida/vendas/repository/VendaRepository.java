package com.vendadistribuida.vendas.repository;

import com.vendadistribuida.vendas.domain.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByUsuarioId(Long usuarioId);

    Page<Venda> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Venda> findByStatus(Venda.StatusVenda status, Pageable pageable);
}
