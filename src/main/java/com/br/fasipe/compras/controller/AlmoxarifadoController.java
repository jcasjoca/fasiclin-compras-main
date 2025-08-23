package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Almoxarifado;
import com.br.fasipe.compras.service.AlmoxarifadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/almoxarifado")
public class AlmoxarifadoController {

    @Autowired
    private AlmoxarifadoService almoxarifadoService;
    
    @GetMapping
    public ResponseEntity<List<Almoxarifado>> listarTodos() {
        List<Almoxarifado> almoxarifados = almoxarifadoService.listarTodos();
        return ResponseEntity.ok(almoxarifados);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Almoxarifado> buscarPorId(@PathVariable Integer id) {
        Optional<Almoxarifado> almoxarifado = almoxarifadoService.buscarPorId(id);
        
        if (almoxarifado.isPresent()) {
            return ResponseEntity.ok(almoxarifado.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/setor/{idSetor}")
    public ResponseEntity<Almoxarifado> buscarPorIdSetor(@PathVariable Integer idSetor) {
        Almoxarifado almoxarifado = almoxarifadoService.buscarPorIdSetor(idSetor);
        
        if (almoxarifado != null) {
            return ResponseEntity.ok(almoxarifado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/nome")
    public ResponseEntity<List<Almoxarifado>> buscarPorNome(@RequestParam String nome) {
        List<Almoxarifado> almoxarifados = almoxarifadoService.buscarPorNome(nome);
        return ResponseEntity.ok(almoxarifados);
    }
    
    @PostMapping
    public ResponseEntity<Almoxarifado> criar(@RequestBody Almoxarifado almoxarifado) {
        Almoxarifado novoAlmoxarifado = almoxarifadoService.salvar(almoxarifado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAlmoxarifado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Almoxarifado> atualizar(@PathVariable Integer id, @RequestBody Almoxarifado almoxarifado) {
        Optional<Almoxarifado> almoxarifadoExistente = almoxarifadoService.buscarPorId(id);
        
        if (almoxarifadoExistente.isPresent()) {
            almoxarifado.setId(id);
            Almoxarifado almoxarifadoAtualizado = almoxarifadoService.salvar(almoxarifado);
            return ResponseEntity.ok(almoxarifadoAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        try {
            Optional<Almoxarifado> almoxarifado = almoxarifadoService.buscarPorId(id);
            
            if (almoxarifado.isPresent()) {
                almoxarifadoService.excluir(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
