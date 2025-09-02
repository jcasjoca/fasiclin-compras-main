package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fornecedores")
@CrossOrigin(origins = "*")
public class FornecedorController {
    
    @Autowired
    private FornecedorService fornecedorService;
    
    @GetMapping
    public ResponseEntity<List<Fornecedor>> getAllFornecedores() {
        try {
            List<Fornecedor> fornecedores = fornecedorService.findAll();
            return ResponseEntity.ok(fornecedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> getFornecedorById(@PathVariable Integer id) {
        try {
            Optional<Fornecedor> fornecedor = fornecedorService.findById(id);
            return fornecedor.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<Fornecedor> getFornecedorByIdPessoa(@PathVariable Integer idPessoa) {
        try {
            Optional<Fornecedor> fornecedor = fornecedorService.findByIdPessoa(idPessoa);
            return fornecedor.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/representante")
    public ResponseEntity<List<Fornecedor>> getFornecedoresByRepresentante(@RequestParam String representante) {
        try {
            List<Fornecedor> fornecedores = fornecedorService.findByRepresentante(representante);
            return ResponseEntity.ok(fornecedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/descricao")
    public ResponseEntity<List<Fornecedor>> getFornecedoresByDescricao(@RequestParam String descricao) {
        try {
            List<Fornecedor> fornecedores = fornecedorService.findByDescricao(descricao);
            return ResponseEntity.ok(fornecedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Fornecedor> createFornecedor(@RequestBody Fornecedor fornecedor) {
        try {
            Fornecedor savedFornecedor = fornecedorService.save(fornecedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFornecedor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> updateFornecedor(@PathVariable Integer id, @RequestBody Fornecedor fornecedor) {
        try {
            Fornecedor updatedFornecedor = fornecedorService.update(id, fornecedor);
            return ResponseEntity.ok(updatedFornecedor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFornecedor(@PathVariable Integer id) {
        try {
            fornecedorService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
