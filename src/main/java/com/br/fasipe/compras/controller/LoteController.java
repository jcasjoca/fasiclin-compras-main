package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.service.LoteService;
import com.br.fasipe.compras.service.OrdemCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lote")
public class LoteController {

    @Autowired
    private LoteService loteService;
    
    @Autowired
    private OrdemCompraService ordemCompraService;
    
    @GetMapping
    public ResponseEntity<List<Lote>> listarTodos() {
        List<Lote> lotes = loteService.listarTodos();
        return ResponseEntity.ok(lotes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Lote> buscarPorId(@PathVariable Integer id) {
        Optional<Lote> lote = loteService.buscarPorId(id);
        
        if (lote.isPresent()) {
            return ResponseEntity.ok(lote.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/ordemcompra/{ordemCompraId}")
    public ResponseEntity<List<Lote>> buscarPorOrdemCompra(@PathVariable Integer ordemCompraId) {
        Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(ordemCompraId);
        
        if (ordemCompra.isPresent()) {
            List<Lote> lotes = loteService.buscarPorOrdemCompra(ordemCompra.get());
            return ResponseEntity.ok(lotes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/vencimento")
    public ResponseEntity<List<Lote>> buscarPorVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataLimite) {
        List<Lote> lotes = loteService.buscarPorVencimento(dataLimite);
        return ResponseEntity.ok(lotes);
    }
    
    @GetMapping("/avencer")
    public ResponseEntity<List<Lote>> buscarLotesAVencer(@RequestParam(defaultValue = "30") int dias) {
        List<Lote> lotes = loteService.buscarLotesAVencer(dias);
        return ResponseEntity.ok(lotes);
    }
    
    @PostMapping
    public ResponseEntity<Lote> criar(@RequestBody Lote lote) {
        // Validar se a ordem de compra existe
        if (lote.getOrdemCompra() != null && lote.getOrdemCompra().getId() != null) {
            Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(lote.getOrdemCompra().getId());
            if (!ordemCompra.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        Lote novoLote = loteService.salvar(lote);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoLote);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Lote> atualizar(@PathVariable Integer id, @RequestBody Lote lote) {
        Optional<Lote> loteExistente = loteService.buscarPorId(id);
        
        if (loteExistente.isPresent()) {
            lote.setId(id);
            Lote loteAtualizado = loteService.salvar(lote);
            return ResponseEntity.ok(loteAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<Lote> atualizarQuantidade(@PathVariable Integer id, @RequestParam Integer quantidade) {
        try {
            loteService.atualizarQuantidade(id, quantidade);
            Optional<Lote> loteAtualizado = loteService.buscarPorId(id);
            
            if (loteAtualizado.isPresent()) {
                return ResponseEntity.ok(loteAtualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<Lote> lote = loteService.buscarPorId(id);
            
            if (lote.isPresent()) {
                loteService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
