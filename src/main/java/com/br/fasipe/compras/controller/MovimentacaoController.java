package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.model.Movimentacao.TipoMovimentacao;
import com.br.fasipe.compras.service.EstoqueService;
import com.br.fasipe.compras.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimentacao")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;
    
    @Autowired
    private EstoqueService estoqueService;
    
    @GetMapping
    public ResponseEntity<List<Movimentacao>> listarTodos() {
        List<Movimentacao> movimentacoes = movimentacaoService.listarTodas();
        return ResponseEntity.ok(movimentacoes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Movimentacao> buscarPorId(@PathVariable Integer id) {
        Optional<Movimentacao> movimentacao = movimentacaoService.buscarPorId(id);
        
        if (movimentacao.isPresent()) {
            return ResponseEntity.ok(movimentacao.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estoque/{estoqueId}")
    public ResponseEntity<List<Movimentacao>> buscarPorEstoque(@PathVariable Integer estoqueId) {
        Optional<Estoque> estoque = estoqueService.buscarPorId(estoqueId);
        
        if (estoque.isPresent()) {
            List<Movimentacao> movimentacoes = movimentacaoService.buscarPorEstoque(estoque.get());
            return ResponseEntity.ok(movimentacoes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/tipo")
    public ResponseEntity<List<Movimentacao>> buscarPorTipo(@RequestParam TipoMovimentacao tipo) {
        List<Movimentacao> movimentacoes = movimentacaoService.buscarPorTipo(tipo);
        return ResponseEntity.ok(movimentacoes);
    }
    
    @GetMapping("/data")
    public ResponseEntity<List<Movimentacao>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Movimentacao> movimentacoes = movimentacaoService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(movimentacoes);
    }
    
    @GetMapping("/setor-origem/{idSetorOrigem}")
    public ResponseEntity<List<Movimentacao>> buscarPorSetorOrigem(@PathVariable Integer idSetorOrigem) {
        List<Movimentacao> movimentacoes = movimentacaoService.buscarPorSetorOrigem(idSetorOrigem);
        return ResponseEntity.ok(movimentacoes);
    }
    
    @GetMapping("/setor-destino/{idSetorDestino}")
    public ResponseEntity<List<Movimentacao>> buscarPorSetorDestino(@PathVariable Integer idSetorDestino) {
        List<Movimentacao> movimentacoes = movimentacaoService.buscarPorSetorDestino(idSetorDestino);
        return ResponseEntity.ok(movimentacoes);
    }
    
    @PostMapping
    public ResponseEntity<Movimentacao> criar(@RequestBody Movimentacao movimentacao) {
        // Validar se o estoque existe
        if (movimentacao.getEstoque() == null || movimentacao.getEstoque().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Estoque> estoque = estoqueService.buscarPorId(movimentacao.getEstoque().getId());
        
        if (!estoque.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        Movimentacao novaMovimentacao = movimentacaoService.salvar(movimentacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaMovimentacao);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        Optional<Movimentacao> movimentacao = movimentacaoService.buscarPorId(id);
        
        if (movimentacao.isPresent()) {
            movimentacaoService.excluir(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
