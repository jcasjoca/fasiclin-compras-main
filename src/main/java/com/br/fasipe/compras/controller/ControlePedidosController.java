package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.ControlePedidos;
import com.br.fasipe.compras.service.ControlePedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/controle-pedidos")
@CrossOrigin(origins = "*")
public class ControlePedidosController {
    
    @Autowired
    private ControlePedidosService controlePedidosService;
    
    @GetMapping
    public ResponseEntity<List<ControlePedidos>> getAllControlePedidos() {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findAll();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ControlePedidos> getControlePedidosById(@PathVariable Integer id) {
        try {
            Optional<ControlePedidos> pedido = controlePedidosService.findById(id);
            return pedido.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<ControlePedidos> getControlePedidosByNumero(@PathVariable String numeroPedido) {
        try {
            Optional<ControlePedidos> pedido = controlePedidosService.findByNumeroPedido(numeroPedido);
            return pedido.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<List<ControlePedidos>> getPedidosByFornecedor(@PathVariable Integer fornecedorId) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByFornecedorId(fornecedorId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/ordem-compra/{ordemCompraId}")
    public ResponseEntity<List<ControlePedidos>> getPedidosByOrdemCompra(@PathVariable Integer ordemCompraId) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByOrdemCompraId(ordemCompraId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/status/{statusPedido}")
    public ResponseEntity<List<ControlePedidos>> getPedidosByStatus(@PathVariable ControlePedidos.StatusPedido statusPedido) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByStatusPedido(statusPedido);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/tipo-pagamento/{tipoPagamento}")
    public ResponseEntity<List<ControlePedidos>> getPedidosByTipoPagamento(@PathVariable ControlePedidos.TipoPagamento tipoPagamento) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByTipoPagamento(tipoPagamento);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-pedido")
    public ResponseEntity<List<ControlePedidos>> getPedidosByPeriodoPedido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByPeriodoPedido(dataInicio, dataFim);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-prevista-entrega")
    public ResponseEntity<List<ControlePedidos>> getPedidosByPeriodoPrevistaEntrega(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByPeriodoPrevistaEntrega(dataInicio, dataFim);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-real-entrega")
    public ResponseEntity<List<ControlePedidos>> getPedidosByPeriodoRealEntrega(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByPeriodoRealEntrega(dataInicio, dataFim);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/valor-range")
    public ResponseEntity<List<ControlePedidos>> getPedidosByValorRange(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByValorRange(valorMinimo, valorMaximo);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/nota-fiscal/{numeroNotaFiscal}")
    public ResponseEntity<ControlePedidos> getPedidoByNotaFiscal(@PathVariable String numeroNotaFiscal) {
        try {
            Optional<ControlePedidos> pedido = controlePedidosService.findByNumeroNotaFiscal(numeroNotaFiscal);
            return pedido.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/chave-nfe/{chaveNfe}")
    public ResponseEntity<ControlePedidos> getPedidoByChaveNfe(@PathVariable String chaveNfe) {
        try {
            Optional<ControlePedidos> pedido = controlePedidosService.findByChaveNfe(chaveNfe);
            return pedido.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/usuario/{idUsuarioResponsavel}")
    public ResponseEntity<List<ControlePedidos>> getPedidosByUsuario(@PathVariable Integer idUsuarioResponsavel) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByUsuarioResponsavel(idUsuarioResponsavel);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/atrasados")
    public ResponseEntity<List<ControlePedidos>> getPedidosAtrasados() {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findPedidosAtrasados();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencendo-hoje")
    public ResponseEntity<List<ControlePedidos>> getPedidosVencendoHoje() {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findPedidosVencendoHoje();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/vencendo")
    public ResponseEntity<List<ControlePedidos>> getPedidosVencendo(@RequestParam(defaultValue = "7") int dias) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findPedidosVencendoEm(dias);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/entregues-sem-nota-fiscal")
    public ResponseEntity<List<ControlePedidos>> getPedidosEntreguesSemNotaFiscal() {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findPedidosEntreguesSemNotaFiscal();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/valor-maior-que/{valorMinimo}")
    public ResponseEntity<List<ControlePedidos>> getPedidosComValorMaiorQue(@PathVariable BigDecimal valorMinimo) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findPedidosComValorMaiorQue(valorMinimo);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/periodo-criacao")
    public ResponseEntity<List<ControlePedidos>> getPedidosByPeriodoCriacao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<ControlePedidos> pedidos = controlePedidosService.findByCriacaoPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/soma-valor-por-status/{statusPedido}")
    public ResponseEntity<BigDecimal> getSomaValorTotalByStatus(@PathVariable ControlePedidos.StatusPedido statusPedido) {
        try {
            BigDecimal soma = controlePedidosService.getSomaValorTotalByStatus(statusPedido);
            return ResponseEntity.ok(soma);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/count-por-status/{statusPedido}")
    public ResponseEntity<Long> getCountByStatus(@PathVariable ControlePedidos.StatusPedido statusPedido) {
        try {
            Long count = controlePedidosService.countByStatusPedido(statusPedido);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/valor-medio-entregues")
    public ResponseEntity<BigDecimal> getValorMedioPedidosEntregues() {
        try {
            BigDecimal valorMedio = controlePedidosService.getValorMedioPedidosEntregues();
            return ResponseEntity.ok(valorMedio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<ControlePedidos> createControlePedidos(@RequestBody ControlePedidos controlePedidos) {
        try {
            ControlePedidos savedPedido = controlePedidosService.save(controlePedidos);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ControlePedidos> updateControlePedidos(@PathVariable Integer id, @RequestBody ControlePedidos controlePedidos) {
        try {
            ControlePedidos updatedPedido = controlePedidosService.update(id, controlePedidos);
            return ResponseEntity.ok(updatedPedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ControlePedidos> updateStatus(@PathVariable Integer id, @RequestBody ControlePedidos.StatusPedido novoStatus) {
        try {
            ControlePedidos updatedPedido = controlePedidosService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(updatedPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PatchMapping("/{id}/nota-fiscal")
    public ResponseEntity<ControlePedidos> adicionarNotaFiscal(
            @PathVariable Integer id,
            @RequestParam String numeroNotaFiscal,
            @RequestParam(required = false) String chaveNfe) {
        try {
            ControlePedidos updatedPedido = controlePedidosService.adicionarNotaFiscal(id, numeroNotaFiscal, chaveNfe);
            return ResponseEntity.ok(updatedPedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteControlePedidos(@PathVariable Integer id) {
        try {
            controlePedidosService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
