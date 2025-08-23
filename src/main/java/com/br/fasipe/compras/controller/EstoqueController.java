package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.service.EstoqueService;
import com.br.fasipe.compras.service.LoteService;
import com.br.fasipe.compras.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private LoteService loteService;
    
    @GetMapping
    public ResponseEntity<List<Estoque>> listarTodos() {
        List<Estoque> estoques = estoqueService.listarTodos();
        return ResponseEntity.ok(estoques);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable Integer id) {
        Optional<Estoque> estoque = estoqueService.buscarPorId(id);
        
        if (estoque.isPresent()) {
            return ResponseEntity.ok(estoque.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<Estoque>> buscarPorProduto(@PathVariable Integer produtoId) {
        Optional<Produto> produto = produtoService.buscarPorId(produtoId);
        
        if (produto.isPresent()) {
            List<Estoque> estoques = estoqueService.buscarPorProduto(produto.get());
            return ResponseEntity.ok(estoques);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<Estoque>> buscarPorLote(@PathVariable Integer loteId) {
        Optional<Lote> lote = loteService.buscarPorId(loteId);
        
        if (lote.isPresent()) {
            List<Estoque> estoques = estoqueService.buscarPorLote(lote.get());
            return ResponseEntity.ok(estoques);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/produto/{produtoId}/quantidade")
    public ResponseEntity<Integer> obterQuantidadeTotalPorProduto(@PathVariable Integer produtoId) {
        Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
        
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Integer quantidade = estoqueService.obterQuantidadeTotalPorProduto(produto);
            return ResponseEntity.ok(quantidade);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Estoque> criar(@RequestBody Estoque estoque) {
        // Validar se o produto e o lote existem
        if (estoque.getProduto() == null || estoque.getProduto().getId() == null ||
            estoque.getLote() == null || estoque.getLote().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Produto> produto = produtoService.buscarPorId(estoque.getProduto().getId());
        Optional<Lote> lote = loteService.buscarPorId(estoque.getLote().getId());
        
        if (!produto.isPresent() || !lote.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        Estoque novoEstoque = estoqueService.salvar(estoque);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEstoque);
    }
    
    @PostMapping("/adicionar")
    public ResponseEntity<Estoque> adicionarEstoque(
            @RequestParam Integer produtoId,
            @RequestParam Integer loteId,
            @RequestParam Integer quantidade,
            @RequestParam Integer idUsuario,
            @RequestParam Integer idSetorOrigem) {
        try {
            Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
            Optional<Lote> loteOpt = loteService.buscarPorId(loteId);
            
            if (produtoOpt.isPresent() && loteOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                Lote lote = loteOpt.get();
                
                estoqueService.adicionarEstoque(produto, lote, quantidade, idUsuario, idSetorOrigem);
                
                // Buscar o estoque atualizado
                List<Estoque> estoques = estoqueService.buscarPorLote(lote);
                if (!estoques.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(estoques.get(0));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/retirar")
    public ResponseEntity<Void> retirarEstoque(
            @RequestParam Integer produtoId,
            @RequestParam Integer quantidade,
            @RequestParam Integer idUsuario,
            @RequestParam Integer idSetorOrigem,
            @RequestParam Integer idSetorDestino) {
        try {
            Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
            
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                estoqueService.retirarEstoque(produto, quantidade, idUsuario, idSetorOrigem, idSetorDestino);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Estoque> atualizar(@PathVariable Integer id, @RequestBody Estoque estoque) {
        Optional<Estoque> estoqueExistente = estoqueService.buscarPorId(id);
        
        if (estoqueExistente.isPresent()) {
            estoque.setId(id);
            Estoque estoqueAtualizado = estoqueService.salvar(estoque);
            return ResponseEntity.ok(estoqueAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<Estoque> estoque = estoqueService.buscarPorId(id);
            
            if (estoque.isPresent()) {
                estoqueService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
