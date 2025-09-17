package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.dto.OrcamentoDTO;
import com.br.fasipe.compras.service.OrdemDeCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrdemDeCompraController {
    
    @Autowired
    private OrdemDeCompraService ordemDeCompraService;
    
    @GetMapping("/orcamentos/pendentes")
    public ResponseEntity<List<OrcamentoDTO>> buscarOrcamentosPendentes() {
        try {
            List<OrcamentoDTO> orcamentos = ordemDeCompraService.buscarOrcamentosPendentes();
            return ResponseEntity.ok(orcamentos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/ordens-de-compra/gerar")
    public ResponseEntity<byte[]> gerarOrdensDeCompra(@RequestBody List<Long> orcamentoIds) {
        try {
            byte[] zipBytes = ordemDeCompraService.processarEGerarOrdens(orcamentoIds);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "OrdensDeCompra.zip");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);
                
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/ordens-de-compra")
    public ResponseEntity<List<OrcamentoDTO>> consultarOrdensDeCompra(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(required = false) Integer fornecedorId,
            @RequestParam(required = false) Integer produtoId) {
        
        try {
            if (dataInicial.isAfter(dataFinal)) {
                return ResponseEntity.badRequest().build();
            }
            
            List<OrcamentoDTO> orcamentos = ordemDeCompraService.consultarOrdensDeCompra(
                dataInicial, dataFinal, fornecedorId, produtoId);
                
            return ResponseEntity.ok(orcamentos);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}