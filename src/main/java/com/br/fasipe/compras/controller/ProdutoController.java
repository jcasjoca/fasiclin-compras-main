package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Almoxarifado;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.service.AlmoxarifadoService;
import com.br.fasipe.compras.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;
    
    @Autowired
    private AlmoxarifadoService almoxarifadoService;
    
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Integer id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        
        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/nome")
    public ResponseEntity<List<Produto>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/almoxarifado/{almoxarifadoId}")
    public ResponseEntity<List<Produto>> buscarPorAlmoxarifado(@PathVariable Integer almoxarifadoId) {
        Optional<Almoxarifado> almoxarifado = almoxarifadoService.buscarPorId(almoxarifadoId);
        
        if (almoxarifado.isPresent()) {
            List<Produto> produtos = produtoService.buscarPorAlmoxarifado(almoxarifado.get());
            return ResponseEntity.ok(produtos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Produto>> buscarProdutosComEstoqueBaixo() {
        List<Produto> produtos = produtoService.buscarProdutosComEstoqueBaixo();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/{id}/estoque")
    public ResponseEntity<Integer> obterQuantidadeEmEstoque(@PathVariable Integer id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        
        if (produto.isPresent()) {
            Integer quantidade = produtoService.obterQuantidadeEmEstoque(id);
            return ResponseEntity.ok(quantidade);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/necessidade-compra")
    public ResponseEntity<Boolean> verificarNecessidadeCompra(@PathVariable Integer id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        
        if (produto.isPresent()) {
            Boolean necessitaCompra = produtoService.verificarNecessidadeCompra(id);
            return ResponseEntity.ok(necessitaCompra);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Integer id, @RequestBody Produto produto) {
        Optional<Produto> produtoExistente = produtoService.buscarPorId(id);
        
        if (produtoExistente.isPresent()) {
            produto.setId(id);
            Produto produtoAtualizado = produtoService.salvar(produto);
            return ResponseEntity.ok(produtoAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<Produto> produto = produtoService.buscarPorId(id);
            
            if (produto.isPresent()) {
                produtoService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
