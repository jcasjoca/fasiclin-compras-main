package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Item_OrdComp;
import com.br.fasipe.compras.service.ItemOrdCompService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/itens-ordem-compra")
@CrossOrigin(origins = "*")
public class ItemOrdCompController {
    
    @Autowired
    private ItemOrdCompService itemOrdCompService;
    
    @GetMapping
    public ResponseEntity<List<Item_OrdComp>> getAllItensOrdemCompra() {
        try {
            List<Item_OrdComp> itens = itemOrdCompService.findAll();
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Item_OrdComp> getItemOrdemCompraById(@PathVariable Integer id) {
        try {
            Optional<Item_OrdComp> item = itemOrdCompService.findById(id);
            return item.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordem-compra/{ordemCompraId}")
    public ResponseEntity<List<Item_OrdComp>> getItensByOrdemCompra(@PathVariable Integer ordemCompraId) {
        try {
            List<Item_OrdComp> itens = itemOrdCompService.findByOrdemCompraId(ordemCompraId);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<Item_OrdComp>> getItensByProduto(@PathVariable Integer produtoId) {
        try {
            List<Item_OrdComp> itens = itemOrdCompService.findByProdutoId(produtoId);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencimento-anterior")
    public ResponseEntity<List<Item_OrdComp>> getItensVencimentoAnterior(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento) {
        try {
            List<Item_OrdComp> itens = itemOrdCompService.findByDataVencimentoAnterior(dataVencimento);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencendo")
    public ResponseEntity<List<Item_OrdComp>> getItensVencendo(@RequestParam(defaultValue = "30") int diasAntecedencia) {
        try {
            List<Item_OrdComp> itens = itemOrdCompService.findItensVencendo(diasAntecedencia);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordem-compra/{ordemCompraId}/total-quantidade")
    public ResponseEntity<Integer> getTotalQuantidadeByOrdemCompra(@PathVariable Integer ordemCompraId) {
        try {
            Integer total = itemOrdCompService.getTotalQuantidadeByOrdemCompra(ordemCompraId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Item_OrdComp> createItemOrdemCompra(@RequestBody Item_OrdComp itemOrdComp) {
        try {
            Item_OrdComp savedItem = itemOrdCompService.save(itemOrdComp);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Item_OrdComp> updateItemOrdemCompra(@PathVariable Integer id, @RequestBody Item_OrdComp itemOrdComp) {
        try {
            Item_OrdComp updatedItem = itemOrdCompService.update(id, itemOrdComp);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemOrdemCompra(@PathVariable Integer id) {
        try {
            itemOrdCompService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
