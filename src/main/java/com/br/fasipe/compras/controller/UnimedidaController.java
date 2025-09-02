package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Unimedida;
import com.br.fasipe.compras.service.UnimedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/unidades-medida")
@CrossOrigin(origins = "*")
public class UnimedidaController {
    
    @Autowired
    private UnimedidaService unimedidaService;
    
    @GetMapping
    public ResponseEntity<List<Unimedida>> getAllUnidadesMedida() {
        try {
            List<Unimedida> unidadesMedida = unimedidaService.findAll();
            return ResponseEntity.ok(unidadesMedida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordenado")
    public ResponseEntity<List<Unimedida>> getAllUnidadesMedidaOrdenado() {
        try {
            List<Unimedida> unidadesMedida = unimedidaService.findAllOrderByDescricao();
            return ResponseEntity.ok(unidadesMedida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Unimedida> getUnidadeMedidaById(@PathVariable Integer id) {
        try {
            Optional<Unimedida> unidadeMedida = unimedidaService.findById(id);
            return unidadeMedida.map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/abreviacao/{unidadeAbreviacao}")
    public ResponseEntity<Unimedida> getUnidadeMedidaByAbreviacao(@PathVariable String unidadeAbreviacao) {
        try {
            Optional<Unimedida> unidadeMedida = unimedidaService.findByUnidadeAbreviacao(unidadeAbreviacao);
            return unidadeMedida.map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/descricao")
    public ResponseEntity<List<Unimedida>> getUnidadesMedidaByDescricao(@RequestParam String descricao) {
        try {
            List<Unimedida> unidadesMedida = unimedidaService.findByDescricao(descricao);
            return ResponseEntity.ok(unidadesMedida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Unimedida>> searchUnidadesMedida(@RequestParam String termo) {
        try {
            List<Unimedida> unidadesMedida = unimedidaService.searchByDescricao(termo);
            return ResponseEntity.ok(unidadesMedida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/exists/abreviacao/{unidadeAbreviacao}")
    public ResponseEntity<Boolean> existsByUnidadeAbreviacao(@PathVariable String unidadeAbreviacao) {
        try {
            boolean exists = unimedidaService.existsByUnidadeAbreviacao(unidadeAbreviacao);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Unimedida> createUnidadeMedida(@RequestBody Unimedida unidadeMedida) {
        try {
            Unimedida savedUnidadeMedida = unimedidaService.save(unidadeMedida);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUnidadeMedida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Unimedida> updateUnidadeMedida(@PathVariable Integer id, @RequestBody Unimedida unidadeMedida) {
        try {
            Unimedida updatedUnidadeMedida = unimedidaService.update(id, unidadeMedida);
            return ResponseEntity.ok(updatedUnidadeMedida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnidadeMedida(@PathVariable Integer id) {
        try {
            unimedidaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
