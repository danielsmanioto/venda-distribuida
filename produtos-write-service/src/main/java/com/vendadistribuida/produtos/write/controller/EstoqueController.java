package com.vendadistribuida.produtos.write.controller;

import com.vendadistribuida.produtos.write.domain.dto.*;
import com.vendadistribuida.produtos.write.domain.enums.TipoMovimentacao;
import com.vendadistribuida.produtos.write.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/produtos/{id}/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/entrada")
    public ResponseEntity<EntradaEstoqueResponse> registrarEntrada(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request
    ) {
        log.info("POST /produtos/{}/estoque/entrada - request={}", id, request);
        EntradaEstoqueResponse response = estoqueService.registrarEntrada(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saida")
    public ResponseEntity<SaidaEstoqueResponse> registrarSaida(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request
    ) {
        log.info("POST /produtos/{}/estoque/saida - request={}", id, request);
        SaidaEstoqueResponse response = estoqueService.registrarSaida(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/saldo")
    public ResponseEntity<SaldoEstoqueResponse> consultarSaldo(@PathVariable Long id) {
        log.info("GET /produtos/{}/estoque/saldo", id);
        SaldoEstoqueResponse response = estoqueService.consultarSaldo(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historico")
    public ResponseEntity<HistoricoEstoqueResponse> consultarHistorico(
            @PathVariable Long id,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        log.info("GET /produtos/{}/estoque/historico - tipo={}, dataInicio={}, dataFim={}, page={}, size={}", 
                id, tipo, dataInicio, dataFim, page, size);
        HistoricoEstoqueResponse response = estoqueService.consultarHistorico(
                id, tipo, dataInicio, dataFim, page, size
        );
        return ResponseEntity.ok(response);
    }
}
