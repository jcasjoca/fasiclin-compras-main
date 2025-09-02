package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin(origins = "*")
public class MovimentacaoController {
    
    @Autowired
    private MovimentacaoService movimentacaoService;
    
    @GetMapping
    public ResponseEntity<List<Movimentacao>> getAllMovimentacoes() {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findAll();
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Movimentacao> getMovimentacaoById(@PathVariable Integer id) {
        try {
            Optional<Movimentacao> movimentacao = movimentacaoService.findById(id);
            return movimentacao.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque/{idEstoque}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesByEstoque(@PathVariable Integer idEstoque) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findByIdEstoque(idEstoque);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesByUsuario(@PathVariable Integer idUsuario) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findByIdUsuario(idUsuario);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/tipo/{tipoMovimentacao}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesByTipo(@PathVariable Movimentacao.TipoMovimentacao tipoMovimentacao) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findByTipoMovimentacao(tipoMovimentacao);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findByPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/setor-origem/{idSetorOrigem}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesBySetorOrigem(@PathVariable Integer idSetorOrigem) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findBySetorOrigem(idSetorOrigem);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/setor-destino/{idSetorDestino}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesBySetorDestino(@PathVariable Integer idSetorDestino) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findBySetorDestino(idSetorDestino);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/hoje")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesHoje() {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findMovimentacoesHoje();
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque/{idEstoque}/tipo/{tipoMovimentacao}")
    public ResponseEntity<List<Movimentacao>> getMovimentacoesByEstoqueAndTipo(
            @PathVariable Integer idEstoque,
            @PathVariable Movimentacao.TipoMovimentacao tipoMovimentacao) {
        try {
            List<Movimentacao> movimentacoes = movimentacaoService.findByEstoqueAndTipo(idEstoque, tipoMovimentacao);
            return ResponseEntity.ok(movimentacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque/{idEstoque}/total-entradas")
    public ResponseEntity<Integer> getTotalEntradasByEstoque(@PathVariable Integer idEstoque) {
        try {
            Integer total = movimentacaoService.getTotalEntradasByEstoque(idEstoque);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque/{idEstoque}/total-saidas")
    public ResponseEntity<Integer> getTotalSaidasByEstoque(@PathVariable Integer idEstoque) {
        try {
            Integer total = movimentacaoService.getTotalSaidasByEstoque(idEstoque);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estoque/{idEstoque}/saldo")
    public ResponseEntity<Integer> getSaldoEstoque(@PathVariable Integer idEstoque) {
        try {
            Integer saldo = movimentacaoService.getSaldoEstoque(idEstoque);
            return ResponseEntity.ok(saldo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Movimentacao> createMovimentacao(@RequestBody Movimentacao movimentacao) {
        try {
            Movimentacao savedMovimentacao = movimentacaoService.save(movimentacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMovimentacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Movimentacao> updateMovimentacao(@PathVariable Integer id, @RequestBody Movimentacao movimentacao) {
        try {
            Movimentacao updatedMovimentacao = movimentacaoService.update(id, movimentacao);
            return ResponseEntity.ok(updatedMovimentacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimentacao(@PathVariable Integer id) {
        try {
            movimentacaoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
