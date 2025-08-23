package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.ItemOrdemCompra;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.service.ItemOrdemCompraService;
import com.br.fasipe.compras.service.OrdemCompraService;
import com.br.fasipe.compras.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/itemordemcompra")
public class ItemOrdemCompraController {

    @Autowired
    private ItemOrdemCompraService itemOrdemCompraService;
    
    @Autowired
    private OrdemCompraService ordemCompraService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping
    public ResponseEntity<List<ItemOrdemCompra>> listarTodos() {
        List<ItemOrdemCompra> itens = itemOrdemCompraService.listarTodos();
        return ResponseEntity.ok(itens);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemOrdemCompra> buscarPorId(@PathVariable Integer id) {
        Optional<ItemOrdemCompra> item = itemOrdemCompraService.buscarPorId(id);
        
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/ordemcompra/{ordemCompraId}")
    public ResponseEntity<List<ItemOrdemCompra>> buscarPorOrdemCompra(@PathVariable Integer ordemCompraId) {
        Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(ordemCompraId);
        
        if (ordemCompra.isPresent()) {
            List<ItemOrdemCompra> itens = itemOrdemCompraService.buscarPorOrdemCompra(ordemCompra.get());
            return ResponseEntity.ok(itens);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<ItemOrdemCompra>> buscarPorProduto(@PathVariable Integer produtoId) {
        Optional<Produto> produto = produtoService.buscarPorId(produtoId);
        
        if (produto.isPresent()) {
            List<ItemOrdemCompra> itens = itemOrdemCompraService.buscarPorProduto(produto.get());
            return ResponseEntity.ok(itens);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<ItemOrdemCompra> criar(@RequestBody ItemOrdemCompra itemOrdemCompra) {
        // Validar se a ordem de compra existe
        if (itemOrdemCompra.getOrdemCompra() != null && itemOrdemCompra.getOrdemCompra().getId() != null) {
            Optional<OrdemCompra> ordemCompra = ordemCompraService.buscarPorId(itemOrdemCompra.getOrdemCompra().getId());
            if (!ordemCompra.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        // Validar se o produto existe
        if (itemOrdemCompra.getProduto() != null && itemOrdemCompra.getProduto().getId() != null) {
            Optional<Produto> produto = produtoService.buscarPorId(itemOrdemCompra.getProduto().getId());
            if (!produto.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        ItemOrdemCompra novoItem = itemOrdemCompraService.salvar(itemOrdemCompra);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ItemOrdemCompra> atualizar(@PathVariable Integer id, @RequestBody ItemOrdemCompra itemOrdemCompra) {
        Optional<ItemOrdemCompra> itemExistente = itemOrdemCompraService.buscarPorId(id);
        
        if (itemExistente.isPresent()) {
            itemOrdemCompra.setId(id);
            ItemOrdemCompra itemAtualizado = itemOrdemCompraService.salvar(itemOrdemCompra);
            return ResponseEntity.ok(itemAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<ItemOrdemCompra> atualizarQuantidade(@PathVariable Integer id, @RequestParam Integer quantidade) {
        try {
            itemOrdemCompraService.atualizarQuantidade(id, quantidade);
            Optional<ItemOrdemCompra> itemAtualizado = itemOrdemCompraService.buscarPorId(id);
            
            if (itemAtualizado.isPresent()) {
                return ResponseEntity.ok(itemAtualizado.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        Optional<ItemOrdemCompra> item = itemOrdemCompraService.buscarPorId(id);
        
        if (item.isPresent()) {
            itemOrdemCompraService.excluir(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
