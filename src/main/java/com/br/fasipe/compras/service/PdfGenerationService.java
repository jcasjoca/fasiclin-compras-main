package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfGenerationService {

    /**
     * CORRIGIDO: Agora gera um PDF válido usando a biblioteca iText7.
     */
    public byte[] gerarPdfUnico(Orcamento orcamento) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // Inicializa o escritor de PDF e o documento
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Adiciona conteúdo ao PDF
            document.add(new Paragraph("Ordem de Compra - ID: " + orcamento.getIdOrcamento()));
            document.add(new Paragraph("Status: " + orcamento.getStatus().toUpperCase()));
            document.add(new Paragraph("Fornecedor: " + orcamento.getFornecedor().getDescricao()));
            document.add(new Paragraph("Produto: " + orcamento.getProduto().getNome()));
            document.add(new Paragraph("Quantidade: " + orcamento.getQuantidade()));
            document.add(new Paragraph("Valor Unitário: R$ " + orcamento.getPrecoCompra()));
            
            // Fecha o documento para finalizar a criação do PDF
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Este método agora usa a função corrigida para gerar PDFs válidos para o ZIP.
     */
    public byte[] gerarPdfsECompactar(Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor, String nomeArquivo) {
         try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (Map.Entry<Fornecedor, List<Orcamento>> entry : orcamentosPorFornecedor.entrySet()) {
                Fornecedor fornecedor = entry.getKey();
                List<Orcamento> orcamentos = entry.getValue();
                
                for (Orcamento orcamento : orcamentos) {
                     byte[] pdfBytes = gerarPdfUnico(orcamento); 
                     
                     if (pdfBytes.length > 0) {
                        String nomeDoArquivoNoZip = String.format("OC_%d_%s.pdf", 
                            orcamento.getIdOrcamento(), 
                            fornecedor.getDescricao().replaceAll("[^a-zA-Z0-9]", "_")
                        );
                        ZipEntry zipEntry = new ZipEntry(nomeDoArquivoNoZip);
                        zipOutputStream.putNextEntry(zipEntry);
                        zipOutputStream.write(pdfBytes);
                        zipOutputStream.closeEntry();
                     }
                }
            }
            zipOutputStream.finish();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0]; 
        }
    }
}

