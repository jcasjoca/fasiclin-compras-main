package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.service.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lotes")
@CrossOrigin(origins = "*")
public class LoteController {
    
    @Autowired
    private LoteService loteService;
    
    @GetMapping
    public ResponseEntity<List<Lote>> getAllLotes() {
        try {
            List<Lote> lotes = loteService.findAll();
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Lote> getLoteById(@PathVariable Integer id) {
        try {
            Optional<Lote> lote = loteService.findById(id);
            return lote.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordem-compra/{ordemCompraId}")
    public ResponseEntity<List<Lote>> getLotesByOrdemCompra(@PathVariable Integer ordemCompraId) {
        try {
            List<Lote> lotes = loteService.findByOrdemCompraId(ordemCompraId);
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencimento-anterior")
    public ResponseEntity<List<Lote>> getLotesVencimentoAnterior(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento) {
        try {
            List<Lote> lotes = loteService.findByDataVencimentoAnterior(dataVencimento);
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<Lote>> getLotesByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<Lote> lotes = loteService.findByPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencendo")
    public ResponseEntity<List<Lote>> getLotesVencendo(@RequestParam(defaultValue = "30") int dias) {
        try {
            List<Lote> lotes = loteService.findLotesVencendoEm(dias);
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencidos")
    public ResponseEntity<List<Lote>> getLotesVencidos() {
        try {
            List<Lote> lotes = loteService.findLotesVencidos();
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencimento-hoje")
    public ResponseEntity<List<Lote>> getLotesVencendoHoje() {
        try {
            List<Lote> lotes = loteService.findLotesVencendoHoje();
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordem-compra/{ordemCompraId}/total-quantidade")
    public ResponseEntity<Integer> getTotalQuantidadeByOrdemCompra(@PathVariable Integer ordemCompraId) {
        try {
            Integer total = loteService.getTotalQuantidadeByOrdemCompra(ordemCompraId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Lote> createLote(@RequestBody Lote lote) {
        try {
            Lote savedLote = loteService.save(lote);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Lote> updateLote(@PathVariable Integer id, @RequestBody Lote lote) {
        try {
            Lote updatedLote = loteService.update(id, lote);
            return ResponseEntity.ok(updatedLote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@PathVariable Integer id) {
        try {
            loteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
