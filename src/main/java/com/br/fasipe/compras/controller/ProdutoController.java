package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping
    public ResponseEntity<List<Produto>> getAllProdutos() {
        try {
            List<Produto> produtos = produtoService.findAll();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Integer id) {
        try {
            Optional<Produto> produto = produtoService.findById(id);
            return produto.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/nome")
    public ResponseEntity<List<Produto>> getProdutosByNome(@RequestParam String nome) {
        try {
            List<Produto> produtos = produtoService.findByNome(nome);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/descricao")
    public ResponseEntity<List<Produto>> getProdutosByDescricao(@RequestParam String descricao) {
        try {
            List<Produto> produtos = produtoService.findByDescricao(descricao);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<Produto> getProdutoByCodigoBarras(@PathVariable String codigoBarras) {
        try {
            Optional<Produto> produto = produtoService.findByCodigoBarras(codigoBarras);
            return produto.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/almoxarifado/{idAlmoxarifado}")
    public ResponseEntity<List<Produto>> getProdutosByAlmoxarifado(@PathVariable Integer idAlmoxarifado) {
        try {
            List<Produto> produtos = produtoService.findByIdAlmoxarifado(idAlmoxarifado);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/unidade-medida/{unidadeMedidaId}")
    public ResponseEntity<List<Produto>> getProdutosByUnidadeMedida(@PathVariable Integer unidadeMedidaId) {
        try {
            List<Produto> produtos = produtoService.findProdutosPorUnidadeMedida(unidadeMedidaId);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/baixo-estoque")
    public ResponseEntity<List<Produto>> getProdutosBaixoEstoque() {
        try {
            List<Produto> produtos = produtoService.findProdutosBaixoEstoque();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/temperatura-ideal")
    public ResponseEntity<List<Produto>> getProdutosByTemperaturaIdeal(
            @RequestParam BigDecimal tempMin,
            @RequestParam BigDecimal tempMax) {
        try {
            List<Produto> produtos = produtoService.findByTemperaturaIdeal(tempMin, tempMax);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque-maximo-menor-que/{limite}")
    public ResponseEntity<List<Produto>> getProdutosByEstoqueMaximoMenorQue(@PathVariable Integer limite) {
        try {
            List<Produto> produtos = produtoService.findByEstoqueMaximoMenorQue(limite);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque-minimo-maior-que/{limite}")
    public ResponseEntity<List<Produto>> getProdutosByEstoqueMinimoMaiorQue(@PathVariable Integer limite) {
        try {
            List<Produto> produtos = produtoService.findByEstoqueMinimoMaiorQue(limite);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/baixo-estoque")
    public ResponseEntity<Boolean> isProdutoComBaixoEstoque(@PathVariable Integer id) {
        try {
            Optional<Produto> produto = produtoService.findById(id);
            if (produto.isPresent()) {
                boolean baixoEstoque = produtoService.isProdutoComBaixoEstoque(produto.get());
                return ResponseEntity.ok(baixoEstoque);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/estoque-critico")
    public ResponseEntity<Boolean> isProdutoComEstoqueCritico(@PathVariable Integer id) {
        try {
            Optional<Produto> produto = produtoService.findById(id);
            if (produto.isPresent()) {
                boolean estoqueCritico = produtoService.isProdutoComEstoqueCritico(produto.get());
                return ResponseEntity.ok(estoqueCritico);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        try {
            Produto savedProduto = produtoService.save(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Integer id, @RequestBody Produto produto) {
        try {
            Produto updatedProduto = produtoService.update(id, produto);
            return ResponseEntity.ok(updatedProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Integer id) {
        try {
            produtoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
