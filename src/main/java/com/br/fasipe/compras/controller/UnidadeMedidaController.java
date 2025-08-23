package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.UnidadeMedida;
import com.br.fasipe.compras.service.UnidadeMedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/unidade-medida")
public class UnidadeMedidaController {

    @Autowired
    private UnidadeMedidaService unidadeMedidaService;
    
    @GetMapping
    public ResponseEntity<List<UnidadeMedida>> listarTodos() {
        List<UnidadeMedida> unidadesMedida = unidadeMedidaService.listarTodas();
        return ResponseEntity.ok(unidadesMedida);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeMedida> buscarPorId(@PathVariable Integer id) {
        Optional<UnidadeMedida> unidadeMedida = unidadeMedidaService.buscarPorId(id);
        
        if (unidadeMedida.isPresent()) {
            return ResponseEntity.ok(unidadeMedida.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/abreviacao/{abreviacao}")
    public ResponseEntity<UnidadeMedida> buscarPorAbreviacao(@PathVariable String abreviacao) {
        UnidadeMedida unidadeMedida = unidadeMedidaService.buscarPorAbreviacao(abreviacao);
        
        if (unidadeMedida != null) {
            return ResponseEntity.ok(unidadeMedida);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/descricao")
    public ResponseEntity<List<UnidadeMedida>> buscarPorDescricao(@RequestParam String descricao) {
        List<UnidadeMedida> unidadesMedida = unidadeMedidaService.buscarPorDescricao(descricao);
        return ResponseEntity.ok(unidadesMedida);
    }
    
    @PostMapping
    public ResponseEntity<UnidadeMedida> criar(@RequestBody UnidadeMedida unidadeMedida) {
        UnidadeMedida novaUnidadeMedida = unidadeMedidaService.salvar(unidadeMedida);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaUnidadeMedida);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeMedida> atualizar(@PathVariable Integer id, @RequestBody UnidadeMedida unidadeMedida) {
        Optional<UnidadeMedida> unidadeMedidaExistente = unidadeMedidaService.buscarPorId(id);
        
        if (unidadeMedidaExistente.isPresent()) {
            unidadeMedida.setId(id);
            UnidadeMedida unidadeMedidaAtualizada = unidadeMedidaService.salvar(unidadeMedida);
            return ResponseEntity.ok(unidadeMedidaAtualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<UnidadeMedida> unidadeMedida = unidadeMedidaService.buscarPorId(id);
            
            if (unidadeMedida.isPresent()) {
                unidadeMedidaService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
