package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfGenerationService {
    
    @Autowired
    private TemplateEngine templateEngine;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public byte[] gerarPdfsECompactar(Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor, String nomeUsuarioAprovador) {
        try {
            ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(zipOutputStream);
            
            for (Map.Entry<Fornecedor, List<Orcamento>> entry : orcamentosPorFornecedor.entrySet()) {
                Fornecedor fornecedor = entry.getKey();
                List<Orcamento> orcamentos = entry.getValue();
                
                byte[] pdfBytes = gerarPdfParaFornecedor(fornecedor, orcamentos, nomeUsuarioAprovador);
                
                String nomeArquivo = String.format("OC_%s_%s.pdf", 
                    fornecedor.getId(), 
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                
                ZipEntry entry1 = new ZipEntry(nomeArquivo);
                zip.putNextEntry(entry1);
                zip.write(pdfBytes);
                zip.closeEntry();
            }
            
            zip.close();
            return zipOutputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDFs e compactar: " + e.getMessage(), e);
        }
    }
    
    private byte[] gerarPdfParaFornecedor(Fornecedor fornecedor, List<Orcamento> orcamentos, String nomeUsuarioAprovador) {
        try {
            Context context = new Context();
            context.setVariable("fornecedor", fornecedor);
            context.setVariable("orcamentos", orcamentos);
            context.setVariable("nomeUsuarioAprovador", nomeUsuarioAprovador);
            context.setVariable("dataGeracao", LocalDate.now().format(DATE_FORMATTER));
            
            // Calcular valores totais
            BigDecimal valorTotal = orcamentos.stream()
                .map(o -> o.getPrecoCompra().multiply(BigDecimal.valueOf(o.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            Integer quantidadeTotal = orcamentos.stream()
                .mapToInt(Orcamento::getQuantidade)
                .sum();
            
            context.setVariable("valorTotal", valorTotal);
            context.setVariable("quantidadeTotal", quantidadeTotal);
            
            // Pegar dados do primeiro orçamento para informações gerais
            if (!orcamentos.isEmpty()) {
                Orcamento primeiro = orcamentos.get(0);
                context.setVariable("dataEntrega", primeiro.getDataEntrega() != null ? 
                    primeiro.getDataEntrega().format(DATE_FORMATTER) : "Não informado");
                context.setVariable("condicoesPagamento", primeiro.getCondicoesPagamento() != null ? 
                    primeiro.getCondicoesPagamento() : "Não informado");
                context.setVariable("garantia", primeiro.getGarantia() != null ? 
                    primeiro.getGarantia() : "Não informado");
            }
            
            String htmlContent = templateEngine.process("ordem_de_compra_template", context);
            
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(pdfOutputStream);
            
            return pdfOutputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF para fornecedor " + fornecedor.getId() + ": " + e.getMessage(), e);
        }
    }
}