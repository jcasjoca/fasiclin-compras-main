package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.model.OrdemCompra.StatusOrdemCompra;
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
@RequestMapping("/api/ordemcompra")
public class OrdemCompraController {

    @Autowired
    private OrdemCompraService ordemCompraService;
    
    @GetMapping
    public ResponseEntity<List<OrdemCompra>> listarTodas() {
        List<OrdemCompra> ordensCompra = ordemCompraService.listarTodas();
        return ResponseEntity.ok(ordensCompra);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrdemCompra> buscarPorId(@PathVariable Integer id) {
        Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(id);
        
        if (ordemCompra.isPresent()) {
            return ResponseEntity.ok(ordemCompra.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrdemCompra>> buscarPorStatus(@PathVariable StatusOrdemCompra status) {
        List<OrdemCompra> ordensCompra = ordemCompraService.buscarPorStatus(status);
        return ResponseEntity.ok(ordensCompra);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<OrdemCompra>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        List<OrdemCompra> ordensCompra = ordemCompraService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(ordensCompra);
    }
    
    @GetMapping("/vencidas")
    public ResponseEntity<List<OrdemCompra>> buscarVencidas() {
        List<OrdemCompra> ordensCompra = ordemCompraService.buscarPorDataPrevistaVencida(LocalDate.now());
        return ResponseEntity.ok(ordensCompra);
    }
    
    @PostMapping
    public ResponseEntity<OrdemCompra> criar(@RequestBody OrdemCompra ordemCompra) {
        OrdemCompra novaOrdemCompra = ordemCompraService.salvar(ordemCompra);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaOrdemCompra);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OrdemCompra> atualizar(@PathVariable Integer id, @RequestBody OrdemCompra ordemCompra) {
        Optional<OrdemCompra> ordemCompraExistente = ordemCompraService.buscarPorId(id);
        
        if (ordemCompraExistente.isPresent()) {
            ordemCompra.setId(id);
            OrdemCompra ordemCompraAtualizada = ordemCompraService.salvar(ordemCompra);
            return ResponseEntity.ok(ordemCompraAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemCompra> atualizarStatus(@PathVariable Integer id, @RequestParam StatusOrdemCompra status) {
        OrdemCompra ordemCompraAtualizada = ordemCompraService.atualizarStatus(id, status);
        
        if (ordemCompraAtualizada != null) {
            return ResponseEntity.ok(ordemCompraAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(id);
        
        if (ordemCompra.isPresent()) {
            ordemCompraService.excluir(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
