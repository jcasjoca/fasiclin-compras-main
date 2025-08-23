package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.FornecedorProduto;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.service.FornecedorProdutoService;
import com.br.fasipe.compras.service.FornecedorService;
import com.br.fasipe.compras.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fornecedor-produto")
public class FornecedorProdutoController {

    @Autowired
    private FornecedorProdutoService fornecedorProdutoService;
    
    @Autowired
    private FornecedorService fornecedorService;
    
    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping
    public ResponseEntity<List<FornecedorProduto>> listarTodos() {
        List<FornecedorProduto> fornecedorProdutos = fornecedorProdutoService.listarTodos();
        return ResponseEntity.ok(fornecedorProdutos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorProduto> buscarPorId(@PathVariable Integer id) {
        Optional<FornecedorProduto> fornecedorProduto = fornecedorProdutoService.buscarPorId(id);
        
        if (fornecedorProduto.isPresent()) {
            return ResponseEntity.ok(fornecedorProduto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<List<FornecedorProduto>> buscarPorFornecedor(@PathVariable Integer fornecedorId) {
        Optional<Fornecedor> fornecedor = fornecedorService.buscarPorId(fornecedorId);
        
        if (fornecedor.isPresent()) {
            List<FornecedorProduto> fornecedorProdutos = fornecedorProdutoService.buscarPorFornecedor(fornecedor.get());
            return ResponseEntity.ok(fornecedorProdutos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<FornecedorProduto>> buscarPorProduto(@PathVariable Integer produtoId) {
        Optional<Produto> produto = produtoService.buscarPorId(produtoId);
        
        if (produto.isPresent()) {
            List<FornecedorProduto> fornecedorProdutos = fornecedorProdutoService.buscarPorProduto(produto.get());
            return ResponseEntity.ok(fornecedorProdutos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<FornecedorProduto> criar(@RequestBody FornecedorProduto fornecedorProduto) {
        // Validar se o fornecedor e o produto existem
        if (fornecedorProduto.getFornecedor() == null || fornecedorProduto.getFornecedor().getId() == null ||
            fornecedorProduto.getProduto() == null || fornecedorProduto.getProduto().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Fornecedor> fornecedor = fornecedorService.buscarPorId(fornecedorProduto.getFornecedor().getId());
        Optional<Produto> produto = produtoService.buscarPorId(fornecedorProduto.getProduto().getId());
        
        if (!fornecedor.isPresent() || !produto.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        FornecedorProduto novoFornecedorProduto = fornecedorProdutoService.salvar(fornecedorProduto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFornecedorProduto);
    }
    
    @PostMapping("/associar")
    public ResponseEntity<Void> associarProdutoFornecedor(
            @RequestParam Integer fornecedorId, 
            @RequestParam Integer produtoId) {
        try {
            Optional<Fornecedor> fornecedorOpt = fornecedorService.buscarPorId(fornecedorId);
            Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
            
            if (fornecedorOpt.isPresent() && produtoOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                Produto produto = produtoOpt.get();
                
                fornecedorProdutoService.associarProdutoAFornecedor(produto, fornecedor);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/desassociar")
    public ResponseEntity<Void> desassociarProdutoFornecedor(
            @RequestParam Integer fornecedorId, 
            @RequestParam Integer produtoId) {
        try {
            Optional<Fornecedor> fornecedorOpt = fornecedorService.buscarPorId(fornecedorId);
            Optional<Produto> produtoOpt = produtoService.buscarPorId(produtoId);
            
            if (fornecedorOpt.isPresent() && produtoOpt.isPresent()) {
                Fornecedor fornecedor = fornecedorOpt.get();
                Produto produto = produtoOpt.get();
                
                fornecedorProdutoService.desassociarProdutoDeFornecedor(produto, fornecedor);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        Optional<FornecedorProduto> fornecedorProduto = fornecedorProdutoService.buscarPorId(id);
        
        if (fornecedorProduto.isPresent()) {
            fornecedorProdutoService.excluir(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
