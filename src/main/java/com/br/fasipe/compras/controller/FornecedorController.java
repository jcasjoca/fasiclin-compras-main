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
@RequestMapping("/api/fornecedor")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;
    
    @GetMapping
    public ResponseEntity<List<Fornecedor>> listarTodos() {
        List<Fornecedor> fornecedores = fornecedorService.listarTodos();
        return ResponseEntity.ok(fornecedores);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Integer id) {
        Optional<Fornecedor> fornecedor = fornecedorService.buscarPorId(id);
        
        if (fornecedor.isPresent()) {
            return ResponseEntity.ok(fornecedor.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/pessoa/{idPessoa}")
    public ResponseEntity<Fornecedor> buscarPorIdPessoa(@PathVariable Integer idPessoa) {
        Fornecedor fornecedor = fornecedorService.buscarPorIdPessoa(idPessoa);
        
        if (fornecedor != null) {
            return ResponseEntity.ok(fornecedor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Fornecedor> criar(@RequestBody Fornecedor fornecedor) {
        Fornecedor novoFornecedor = fornecedorService.salvar(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFornecedor);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> atualizar(@PathVariable Integer id, @RequestBody Fornecedor fornecedor) {
        Optional<Fornecedor> fornecedorExistente = fornecedorService.buscarPorId(id);
        
        if (fornecedorExistente.isPresent()) {
            fornecedor.setId(id);
            Fornecedor fornecedorAtualizado = fornecedorService.salvar(fornecedor);
            return ResponseEntity.ok(fornecedorAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<Fornecedor> fornecedor = fornecedorService.buscarPorId(id);
            
            if (fornecedor.isPresent()) {
                fornecedorService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
