package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
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
            String statusOrcamento = orcamento.getStatus() != null ? orcamento.getStatus().toUpperCase() : "PENDENTE";
            document.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            document.add(new Paragraph("              ORDEM DE COMPRA - ORÇAMENTO"));
            document.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("ID do Orçamento: " + orcamento.getIdOrcamento()));
            document.add(new Paragraph("Status: " + statusOrcamento));
            document.add(new Paragraph("Data de Emissão: " + (orcamento.getDataEmissao() != null ? 
                orcamento.getDataEmissao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A")));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Fornecedor: " + orcamento.getFornecedor().getDescricao()));
            document.add(new Paragraph("Produto: " + orcamento.getProduto().getNome()));
            if (orcamento.getProduto().getDescricao() != null) {
                document.add(new Paragraph("Descrição: " + orcamento.getProduto().getDescricao()));
            }
            document.add(new Paragraph("Quantidade: " + orcamento.getQuantidade() + " " + 
                (orcamento.getUnidadeMedida() != null ? orcamento.getUnidadeMedida().getUnidadeAbreviacao() : "")));
            document.add(new Paragraph("Valor Unitário: R$ " + String.format("%.2f", orcamento.getPrecoCompra().doubleValue())));
            document.add(new Paragraph("Valor Total: R$ " + String.format("%.2f", 
                orcamento.getPrecoCompra().doubleValue() * orcamento.getQuantidade())));
            
            if (orcamento.getDataEntrega() != null) {
                document.add(new Paragraph("Data de Entrega: " + 
                    orcamento.getDataEntrega().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
            
            if (orcamento.getCondicoesPagamento() != null && !orcamento.getCondicoesPagamento().trim().isEmpty()) {
                document.add(new Paragraph("Condições de Pagamento: " + orcamento.getCondicoesPagamento()));
            }
            
            if (orcamento.getGarantia() != null && !orcamento.getGarantia().trim().isEmpty()) {
                document.add(new Paragraph("Garantia: " + orcamento.getGarantia()));
            }
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            document.add(new Paragraph("        Documento gerado em: " + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("═══════════════════════════════════════════════════════════"));
            
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

    /**
     * Gera PDF para pedido agrupado seguindo o layout específico solicitado
     */
    public byte[] gerarPdfPedidoAgrupado(com.br.fasipe.compras.dto.PedidoAgrupadoDTO pedido, 
                                        List<Orcamento> orcamentos) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Configurar margens
            document.setMargins(40, 40, 40, 40);
            
            // Título do cabeçalho - CORREÇÃO: Mostrar status dinâmico
            String statusPedido = pedido.getStatus() != null ? pedido.getStatus().toUpperCase() : "PENDENTE";
            document.add(new Paragraph("┌─────────────────────────────────────────────────────────────────────────────────┐")
                .setFontSize(10));
            document.add(new Paragraph("│                                                                                 │")
                .setFontSize(10));
            document.add(new Paragraph(String.format("│  PEDIDO %s Nº: %-45s │", statusPedido, pedido.getIdPedido()))
                .setFontSize(10));
            document.add(new Paragraph("│                                                                                 │")
                .setFontSize(10));
            
            // IDs dos orçamentos
            String orcamentosStr = pedido.getIdOrcamentos().stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(", "));
            document.add(new Paragraph(String.format("│  Orçamento(s) de Origem: %-48s │", orcamentosStr))
                .setFontSize(10));
            document.add(new Paragraph("│                                                                                 │")
                .setFontSize(10));
            
            // Informações do fornecedor e status
            document.add(new Paragraph(String.format("│  Fornecedor:           %-48s │", pedido.getNomeFornecedor()))
                .setFontSize(10));
            document.add(new Paragraph(String.format("│  Status:               %-48s │", pedido.getStatus()))
                .setFontSize(10));
            document.add(new Paragraph("│                                                                                 │")
                .setFontSize(10));
            document.add(new Paragraph("└─────────────────────────────────────────────────────────────────────────────────┘")
                .setFontSize(10));
            
            // Espaçamento
            document.add(new Paragraph(" "));
            
            // Título da tabela de itens
            document.add(new Paragraph("ITENS DO PEDIDO:").setFontSize(10));
            
            // Cabeçalho da tabela
            document.add(new Paragraph("┌───────────────┬──────┬────┬────────────────┬──────────────┐")
                .setFontSize(10));
            document.add(new Paragraph("│ Produto       │ Qtd. │ Un.│ Valor Unit.    │ Valor Total  │")
                .setFontSize(10));
            document.add(new Paragraph("├───────────────┼──────┼────┼────────────────┼──────────────┤")
                .setFontSize(10));
            
            // CORREÇÃO: Agrupar produtos por nome E preço unitário (produtos iguais com preços diferentes ficam separados)
            Map<String, List<Orcamento>> produtosAgrupados = orcamentos.stream()
                .collect(java.util.stream.Collectors.groupingBy(o -> 
                    o.getProduto().getNome() + "_" + o.getPrecoCompra().toString()));
            
            double valorTotalGeral = 0.0;
            
            // Reorganizar para exibir por nome do produto, mas mantendo preços diferentes separados
            Map<String, List<Map.Entry<String, List<Orcamento>>>> produtosPorNome = new LinkedHashMap<>();
            for (Map.Entry<String, List<Orcamento>> entry : produtosAgrupados.entrySet()) {
                String chave = entry.getKey();
                String nomeProduto = chave.substring(0, chave.lastIndexOf("_"));
                if (!produtosPorNome.containsKey(nomeProduto)) {
                    produtosPorNome.put(nomeProduto, new ArrayList<>());
                }
                produtosPorNome.get(nomeProduto).add(entry);
            }
            
            for (Map.Entry<String, List<Map.Entry<String, List<Orcamento>>>> produtoEntry : produtosPorNome.entrySet()) {
                String nomeProduto = produtoEntry.getKey();
                List<Map.Entry<String, List<Orcamento>>> variacoes = produtoEntry.getValue();
                
                for (Map.Entry<String, List<Orcamento>> variacao : variacoes) {
                    List<Orcamento> orcamentosProduto = variacao.getValue();
                    
                    // Somar quantidades dos orçamentos com mesmo produto e mesmo preço
                    int quantidadeTotal = orcamentosProduto.stream()
                        .mapToInt(Orcamento::getQuantidade)
                        .sum();
                    
                    double valorUnitario = orcamentosProduto.get(0).getPrecoCompra().doubleValue();
                    double valorTotalProduto = quantidadeTotal * valorUnitario;
                    valorTotalGeral += valorTotalProduto;
                    
                    String unidade = orcamentosProduto.get(0).getUnidadeMedida().getUnidadeAbreviacao();
                    
                    // Formatar linha da tabela
                    String produtoTruncado = nomeProduto.length() > 13 ? 
                        nomeProduto.substring(0, 13) + "." : nomeProduto;
                    
                    document.add(new Paragraph(String.format("│ %-13s │ %4d │ %2s │ R$ %10.2f │ R$ %9.2f │",
                        produtoTruncado, quantidadeTotal, unidade, valorUnitario, valorTotalProduto))
                        .setFontSize(10));
                }
            }
            
            // Linha de fechamento da tabela
            document.add(new Paragraph("└───────────────┴──────┴────┴────────────────┴──────────────┘")
                .setFontSize(10));
            
            // Total geral
            document.add(new Paragraph(String.format("                        **TOTAL GERAL: R$ %.2f**", valorTotalGeral))
                .setFontSize(10));
            
            // Espaçamento
            document.add(new Paragraph(" "));
            
            // Condições e prazos
            document.add(new Paragraph("CONDIÇÕES E PRAZOS:").setFontSize(10));
            document.add(new Paragraph("───────────────────").setFontSize(10));
            
            // Calcular prazo de entrega (menor data)
            java.time.LocalDate menorDataEntrega = orcamentos.stream()
                .map(Orcamento::getDataEntrega)
                .filter(java.util.Objects::nonNull)
                .min(java.time.LocalDate::compareTo)
                .orElse(null);
            
            if (menorDataEntrega != null) {
                document.add(new Paragraph(String.format("* Prazo de Entrega: %s", 
                    menorDataEntrega.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                    .setFontSize(10));
            }
            
            // Calcular condições de pagamento (prazo mais distante)
            String condicoesPagamento = orcamentos.stream()
                .map(Orcamento::getCondicoesPagamento)
                .filter(java.util.Objects::nonNull)
                .filter(c -> !c.trim().isEmpty())
                .max((c1, c2) -> {
                    // Extrair números das condições para comparar (assumindo formato "X dias")
                    try {
                        int dias1 = Integer.parseInt(c1.replaceAll("\\D+", ""));
                        int dias2 = Integer.parseInt(c2.replaceAll("\\D+", ""));
                        return Integer.compare(dias1, dias2);
                    } catch (NumberFormatException e) {
                        return c1.compareTo(c2);
                    }
                })
                .orElse("Não especificado");
            
            document.add(new Paragraph(String.format("* Condições de Pagamento: %s", condicoesPagamento))
                .setFontSize(10));
            
            // Garantias por produto
            document.add(new Paragraph("* Garantia: ").setFontSize(10));
            
            Map<String, Set<String>> garantiasPorProduto = new HashMap<>();
            for (Orcamento orc : orcamentos) {
                String produto = orc.getProduto().getNome();
                String garantia = orc.getGarantia();
                if (garantia != null && !garantia.trim().isEmpty()) {
                    if (!garantiasPorProduto.containsKey(produto)) {
                        garantiasPorProduto.put(produto, new HashSet<>());
                    }
                    garantiasPorProduto.get(produto).add(garantia);
                }
            }
            
            if (garantiasPorProduto.isEmpty()) {
                document.add(new Paragraph("  Não especificada").setFontSize(10));
            } else {
                for (Map.Entry<String, Set<String>> entry : garantiasPorProduto.entrySet()) {
                    String produto = entry.getKey();
                    Set<String> garantias = entry.getValue();
                    for (String garantia : garantias) {
                        document.add(new Paragraph(String.format("  %s: %s", produto, garantia))
                            .setFontSize(10));
                    }
                }
            }
            
            // Espaçamento
            document.add(new Paragraph(" "));
            document.add(new Paragraph("─────────────────────────────────────────────────────────────────────")
                .setFontSize(10));
            
            // CORREÇÃO: Data/hora FIXA de aprovação (com fallback para registros antigos)
            String dataHoraAprovacao;
            if (pedido.getDataHoraAprovacao() != null) {
                // Usar data/hora fixa de aprovação (para novos registros)
                dataHoraAprovacao = pedido.getDataHoraAprovacao().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else if (pedido.getDataGeracao() != null) {
                // Fallback: usar data de geração com hora FIXA (para registros antigos)
                java.time.LocalDateTime timestampFixo = null;
                if (pedido.getIdOrcamentos() != null && !pedido.getIdOrcamentos().isEmpty()) {
                    timestampFixo = OrdemDeCompraService.getDataHoraAprovacao(pedido.getIdOrcamentos().get(0));
                }
                
                if (timestampFixo != null) {
                    // Usar timestamp fixo armazenado
                    dataHoraAprovacao = timestampFixo.format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                } else {
                    // Usar data original com hora fixa 14:30 para registros antigos
                    dataHoraAprovacao = pedido.getDataGeracao().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 14:30";
                }
            } else {
                // Fallback final
                dataHoraAprovacao = java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            document.add(new Paragraph(String.format("   Ordem de Compra Gerada: %s", dataHoraAprovacao))
                .setFontSize(10));
            
            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Gera ZIP com múltiplos PDFs
     */
    public byte[] gerarZipComPdfs(Map<String, byte[]> pdfs) {
        if (pdfs == null || pdfs.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            for (Map.Entry<String, byte[]> entry : pdfs.entrySet()) {
                String nomeArquivo = entry.getKey();
                byte[] pdfBytes = entry.getValue();
                
                if (pdfBytes.length > 0) {
                    ZipEntry zipEntry = new ZipEntry(nomeArquivo);
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(pdfBytes);
                    zipOutputStream.closeEntry();
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

