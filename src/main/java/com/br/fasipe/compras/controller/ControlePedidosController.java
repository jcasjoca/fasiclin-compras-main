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
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ControlePedidosController {
    
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
    
    @PostMapping("/ordens-de-compra/processar")
    public ResponseEntity<Map<String, String>> processarOrdensDeCompra(@RequestBody List<Long> orcamentoIds) {
        try {
            ordemDeCompraService.processarStatus(orcamentoIds);
            Map<String, String> response = Map.of("message", "Orçamentos processados com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // CORREÇÃO: Adicionados os novos parâmetros valorMinimo e valorMaximo
    @GetMapping("/ordens-de-compra")
    public ResponseEntity<List<OrcamentoDTO>> consultarOrdensDeCompra(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(required = false) String fornecedorNome,
            @RequestParam(required = false) String produtoNome,
            @RequestParam(required = false) Long idOrcamento,
            @RequestParam(required = false) String status) { 
        
        try {
            if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
                return ResponseEntity.badRequest().build();
            }
            
            List<OrcamentoDTO> orcamentos = ordemDeCompraService.consultarOrdensDeCompra(
                dataInicial, dataFinal, fornecedorNome, produtoNome, idOrcamento, status);
                
            return ResponseEntity.ok(orcamentos);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ordens-de-compra/visualizar/{id}")
    public ResponseEntity<byte[]> visualizarPdfUnico(@PathVariable Long id) {
        try {
            byte[] pdfBytes = ordemDeCompraService.gerarPdfUnicoPorId(id);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Define o header para abrir no navegador, e não baixar
            headers.setContentDispositionFormData("inline", "OrdemDeCompra_" + id + ".pdf");
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ordens-de-compra/download")
    public ResponseEntity<byte[]> baixarOrdensDeCompra(@RequestParam List<Long> ids) {
        try {
            byte[] zipBytes = ordemDeCompraService.gerarPdfsPorIds(ids);

            if (zipBytes == null || zipBytes.length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "OrdensDeCompra.zip");
            
            return ResponseEntity.ok().headers(headers).body(zipBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

