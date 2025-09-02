package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.service.OrdemCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordens-compra")
@CrossOrigin(origins = "*")
public class OrdemCompraController {
    
    @Autowired
    private OrdemCompraService ordemCompraService;
    
    @GetMapping
    public ResponseEntity<List<OrdemCompra>> getAllOrdensCompra() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findAll();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrdemCompra> getOrdemCompraById(@PathVariable Integer id) {
        try {
            Optional<OrdemCompra> ordemCompra = ordemCompraService.findById(id);
            return ordemCompra.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdemCompra>> getOrdensByStatus(@PathVariable OrdemCompra.StatusOrdemCompra status) {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findByStatus(status);
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/pendentes")
    public ResponseEntity<List<OrdemCompra>> getOrdensPendentes() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findOrdensPendentes();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/andamento")
    public ResponseEntity<List<OrdemCompra>> getOrdensEmAndamento() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findOrdensEmAndamento();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/concluidas")
    public ResponseEntity<List<OrdemCompra>> getOrdensConcluidas() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findOrdensConcluidas();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/atrasadas")
    public ResponseEntity<List<OrdemCompra>> getOrdensAtrasadas() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findOrdensAtrasadas();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/andamento-sem-entrega")
    public ResponseEntity<List<OrdemCompra>> getOrdensEmAndamentoSemEntrega() {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findOrdensEmAndamentoSemEntrega();
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-ordem")
    public ResponseEntity<List<OrdemCompra>> getOrdensByPeriodoOrdem(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findByPeriodoOrdem(dataInicio, dataFim);
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-previsto")
    public ResponseEntity<List<OrdemCompra>> getOrdensByPeriodoPrevisto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findByPeriodoPrevisto(dataInicio, dataFim);
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-entrega")
    public ResponseEntity<List<OrdemCompra>> getOrdensByPeriodoEntrega(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findByPeriodoEntrega(dataInicio, dataFim);
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/valor-range")
    public ResponseEntity<List<OrdemCompra>> getOrdensByValorRange(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        try {
            List<OrdemCompra> ordensCompra = ordemCompraService.findByValorBetween(valorMinimo, valorMaximo);
            return ResponseEntity.ok(ordensCompra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/valor-medio-concluidas")
    public ResponseEntity<BigDecimal> getValorMedioOrdensConcluidas() {
        try {
            BigDecimal valorMedio = ordemCompraService.getValorMedioOrdensConcluidas();
            return ResponseEntity.ok(valorMedio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count-by-status/{status}")
    public ResponseEntity<Long> getCountByStatus(@PathVariable OrdemCompra.StatusOrdemCompra status) {
        try {
            Long count = ordemCompraService.countByStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<OrdemCompra> createOrdemCompra(@RequestBody OrdemCompra ordemCompra) {
        try {
            OrdemCompra savedOrdemCompra = ordemCompraService.save(ordemCompra);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrdemCompra);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrdemCompra> updateOrdemCompra(@PathVariable Integer id, @RequestBody OrdemCompra ordemCompra) {
        try {
            OrdemCompra updatedOrdemCompra = ordemCompraService.update(id, ordemCompra);
            return ResponseEntity.ok(updatedOrdemCompra);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemCompra> updateStatus(@PathVariable Integer id, @RequestBody OrdemCompra.StatusOrdemCompra novoStatus) {
        try {
            OrdemCompra updatedOrdemCompra = ordemCompraService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(updatedOrdemCompra);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrdemCompra(@PathVariable Integer id) {
        try {
            ordemCompraService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
