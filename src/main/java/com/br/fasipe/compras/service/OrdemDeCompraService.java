package com.br.fasipe.compras.service;

import com.br.fasipe.compras.dto.OrcamentoDTO;
import com.br.fasipe.compras.dto.PedidoAgrupadoDTO;
import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import com.br.fasipe.compras.model.Usuario;
import com.br.fasipe.compras.repository.OrcamentoRepository;
import com.br.fasipe.compras.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrdemDeCompraService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    // Map para armazenar data/hora de aprovação por ID do orçamento
    private static final Map<Long, LocalDateTime> datasHoraAprovacao = new ConcurrentHashMap<>();

    public List<OrcamentoDTO> buscarOrcamentosPendentes() {
        List<Orcamento> orcamentos = orcamentoRepository.findByStatusPendente();
        return orcamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processarStatus(List<Long> orcamentoIdsAprovados) {
        if (orcamentoIdsAprovados == null || orcamentoIdsAprovados.isEmpty()) {
            return;
        }
        Usuario usuarioPadrao = obterUsuarioPadrao();
        List<Orcamento> orcamentosAprovados = orcamentoRepository.findAllById(orcamentoIdsAprovados);
        if (orcamentosAprovados.isEmpty()) {
            return;
        }
        Set<Integer> produtoIds = orcamentosAprovados.stream()
                .map(orcamento -> orcamento.getProduto().getId())
                .collect(Collectors.toSet());
        if (!produtoIds.isEmpty()) {
             orcamentoRepository.reprovarConcorrentes(produtoIds, orcamentoIdsAprovados);
        }
        LocalDate agora = LocalDate.now();
        LocalDateTime agoraComHora = LocalDateTime.now();
        for (Orcamento orcamento : orcamentosAprovados) {
            orcamento.setStatus("aprovado");
            orcamento.setDataGeracao(agora);
            orcamento.setUsuarioAprovador(usuarioPadrao);
            
            // FIXAR data/hora de aprovação no Map (momento exato da aprovação)
            datasHoraAprovacao.put(orcamento.getIdOrcamento(), agoraComHora);
        }
        orcamentoRepository.saveAll(orcamentosAprovados);
    }
    
    public byte[] gerarPdfUnicoPorId(Long orcamentoId) {
        Optional<Orcamento> orcamentoOpt = orcamentoRepository.findById(orcamentoId);
        if (orcamentoOpt.isEmpty()) {
            return new byte[0];
        }

        Orcamento orcamento = orcamentoOpt.get();
        String status = orcamento.getStatus() != null ? orcamento.getStatus().toLowerCase() : "";

        // CORREÇÃO: Permitir PDF para TODOS os status (pendente, aprovado, reprovado)
        // Agora todos os pedidos podem gerar PDF independente do status
        return pdfGenerationService.gerarPdfUnico(orcamento);
    }
    
    public byte[] gerarPdfsPorIds(List<Long> orcamentoIds) {
        if (orcamentoIds == null || orcamentoIds.isEmpty()) {
            return new byte[0];
        }
        
        List<Orcamento> orcamentosParaBaixar = orcamentoRepository.findAllById(orcamentoIds);

        // CORREÇÃO: Permitir download de PDF para TODOS os status (pendente, aprovado, reprovado)
        // Remover filtro restritivo por status
        List<Orcamento> orcamentosValidos = orcamentosParaBaixar.stream()
                .filter(o -> o.getStatus() != null && !o.getStatus().trim().isEmpty())
                .collect(Collectors.toList());

        if (orcamentosValidos.isEmpty()) {
            return new byte[0];
        }
        
        Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor = orcamentosValidos.stream()
                .collect(Collectors.groupingBy(Orcamento::getFornecedor));
                
        return pdfGenerationService.gerarPdfsECompactar(orcamentosPorFornecedor, "Sistema de Compras");
    }

    // CORREÇÃO: Adicionados os novos parâmetros valorMinimo e valorMaximo
    public List<OrcamentoDTO> consultarOrdensDeCompra(LocalDate dataInicial, LocalDate dataFinal, 
                                                      String fornecedorNome, String produtoNome, Long idOrcamento, 
                                                      String status) {
        
        // Limpar status se for vazio, "todos" ou apenas espaços
        if (status != null && (status.trim().isEmpty() || status.trim().equalsIgnoreCase("todos") || status.trim().equals(""))) {
            status = null;
        }
        
        List<Orcamento> orcamentos = orcamentoRepository.findWithFilters(
            dataInicial, dataFinal, fornecedorNome, produtoNome, idOrcamento, status);
            
        return orcamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PedidoAgrupadoDTO> consultarPedidosAgrupados(LocalDate dataInicial, LocalDate dataFinal, 
                                                           String fornecedorNome, String produtoNome, String idPedido, 
                                                           String status) {
        
        // Limpar status se for vazio, "todos" ou apenas espaços
        if (status != null && (status.trim().isEmpty() || status.trim().equalsIgnoreCase("todos") || status.trim().equals(""))) {
            status = null;
        }
        
        // CORREÇÃO CIRÚRGICA: Buscar orçamentos sem filtros problemáticos, aplicar filtros depois
        List<Orcamento> orcamentos;
        
        // Se não há filtros específicos, buscar todos
        if (dataInicial == null && dataFinal == null && fornecedorNome == null && produtoNome == null && status == null) {
            orcamentos = orcamentoRepository.findWithFilters(null, null, null, null, null, null);
        } else {
            // Aplicar filtros básicos na query (funciona bem)
            orcamentos = orcamentoRepository.findWithFilters(
                dataInicial, dataFinal, fornecedorNome, produtoNome, null, status);
        }
            
        // Aceitar todos os status (pendente, aprovado, reprovado)
        List<Orcamento> orcamentosFiltrados = orcamentos;
        
        // CORREÇÃO: Agrupar apenas por Fornecedor + Status para evitar duplicatas
        // Isso corrige os pedidos PED-3 duplicados e melhora a organização
        Map<String, List<Orcamento>> grupos = orcamentosFiltrados.stream()
            .collect(Collectors.groupingBy(o -> 
                o.getFornecedor().getId() + "_" + 
                o.getStatus().toUpperCase()
            ));
        
        List<PedidoAgrupadoDTO> pedidosAgrupados = new ArrayList<>();
        
        for (Map.Entry<String, List<Orcamento>> entry : grupos.entrySet()) {
            List<Orcamento> grupoOrcamentos = entry.getValue();
            if (grupoOrcamentos.isEmpty()) continue;
            
            Orcamento primeiro = grupoOrcamentos.get(0);
            PedidoAgrupadoDTO pedido = new PedidoAgrupadoDTO();
            
            // CORREÇÃO: Gerar ID determinístico baseado nos dados, não na ordem
            // Usar ID do menor orçamento como base para garantir consistência
            Long menorIdOrcamento = grupoOrcamentos.stream()
                .map(Orcamento::getIdOrcamento)
                .min(Long::compareTo)
                .orElse(primeiro.getIdOrcamento());
            
            LocalDate dataParaId = primeiro.getDataGeracao() != null ? primeiro.getDataGeracao() : primeiro.getDataEmissao();
            String dataFormatada = dataParaId.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // Usar parte do hash do menor ID para garantir unicidade e consistência
            int sequencialDeterministico = (int) (menorIdOrcamento % 1000) + 1;
            
            String idPedidoGerado = String.format("PED-%d-%s-%03d", 
                primeiro.getFornecedor().getId(), dataFormatada, sequencialDeterministico);
            pedido.setIdPedido(idPedidoGerado);
            
            // Coletar IDs dos orçamentos
            List<Long> idsOrcamentos = grupoOrcamentos.stream()
                .map(Orcamento::getIdOrcamento)
                .collect(Collectors.toList());
            pedido.setIdOrcamentos(idsOrcamentos);
            
            // Dados do fornecedor
            pedido.setIdFornecedor(primeiro.getFornecedor().getId());
            pedido.setNomeFornecedor(primeiro.getFornecedor().getDescricao());
            
            // Range de datas de emissão
            LocalDate dataMinima = grupoOrcamentos.stream()
                .map(Orcamento::getDataEmissao)
                .min(LocalDate::compareTo)
                .orElse(primeiro.getDataEmissao());
            LocalDate dataMaxima = grupoOrcamentos.stream()
                .map(Orcamento::getDataEmissao)
                .max(LocalDate::compareTo)
                .orElse(primeiro.getDataEmissao());
                
            pedido.setDataEmissaoInicio(dataMinima);
            pedido.setDataEmissaoFim(dataMaxima);
            
            if (dataMinima.equals(dataMaxima)) {
                pedido.setRangeDataEmissao(dataMinima.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                pedido.setRangeDataEmissao(
                    dataMinima.format(DateTimeFormatter.ofPattern("dd/MM")) + " - " +
                    dataMaxima.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }
            
            // Valor total
            Double valorTotal = grupoOrcamentos.stream()
                .mapToDouble(o -> o.getPrecoCompra().doubleValue() * o.getQuantidade())
                .sum();
            pedido.setValorTotal(valorTotal);
            
            // Status e data de geração (usar data emissão para pendentes)
            pedido.setStatus(primeiro.getStatus());
            pedido.setDataGeracao(primeiro.getDataGeracao() != null ? primeiro.getDataGeracao() : primeiro.getDataEmissao());
            
            // Data/hora de aprovação fixa (do Map de aprovações)
            LocalDateTime dataHoraAprovacao = datasHoraAprovacao.get(primeiro.getIdOrcamento());
            pedido.setDataHoraAprovacao(dataHoraAprovacao);
            
            // Usuário aprovador
            if (primeiro.getUsuarioAprovador() != null) {
                pedido.setNomeUsuarioAprovador(primeiro.getUsuarioAprovador().getLoginUsuario());
            }
            
            // Produtos
            pedido.setQuantidadeProdutos(grupoOrcamentos.size());
            List<String> nomesProdutos = grupoOrcamentos.stream()
                .map(o -> o.getProduto().getNome())
                .distinct()
                .collect(Collectors.toList());
            pedido.setNomesProdutos(nomesProdutos);
            
            pedidosAgrupados.add(pedido);
        }
        
        // CORREÇÃO: Aplicar filtros adicionais nos pedidos agrupados para garantir consistência
        final LocalDate dataInicialFinal = dataInicial;
        final LocalDate dataFinalFinal = dataFinal;
        final String fornecedorNomeFinal = fornecedorNome;
        final String produtoNomeFinal = produtoNome;
        final String statusFinal = status;
        
        if (dataInicialFinal != null || dataFinalFinal != null) {
            pedidosAgrupados = pedidosAgrupados.stream()
                .filter(p -> {
                    LocalDate dataPedido = p.getDataEmissaoInicio();
                    if (dataPedido == null) return true;
                    
                    boolean dentroRange = true;
                    if (dataInicialFinal != null) {
                        dentroRange = dentroRange && !dataPedido.isBefore(dataInicialFinal);
                    }
                    if (dataFinalFinal != null) {
                        dentroRange = dentroRange && !dataPedido.isAfter(dataFinalFinal);
                    }
                    return dentroRange;
                })
                .collect(Collectors.toList());
        }
        
        // Filtrar por fornecedor nos pedidos agrupados (mais preciso)
        if (fornecedorNomeFinal != null && !fornecedorNomeFinal.trim().isEmpty()) {
            pedidosAgrupados = pedidosAgrupados.stream()
                .filter(p -> p.getNomeFornecedor().toLowerCase().contains(fornecedorNomeFinal.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Filtrar por produto nos pedidos agrupados (mais preciso)  
        if (produtoNomeFinal != null && !produtoNomeFinal.trim().isEmpty()) {
            pedidosAgrupados = pedidosAgrupados.stream()
                .filter(p -> p.getNomesProdutos().stream()
                    .anyMatch(produto -> produto.toLowerCase().contains(produtoNomeFinal.toLowerCase())))
                .collect(Collectors.toList());
        }
        
        // Filtrar por status nos pedidos agrupados (garantir que funciona)
        if (statusFinal != null && !statusFinal.trim().isEmpty()) {
            pedidosAgrupados = pedidosAgrupados.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(statusFinal.trim()))
                .collect(Collectors.toList());
        }
        
        // Filtrar por ID do pedido se fornecido (PRESERVADO - NÃO ALTERAR)
        if (idPedido != null && !idPedido.trim().isEmpty()) {
            pedidosAgrupados = pedidosAgrupados.stream()
                .filter(p -> p.getIdPedido().toLowerCase().contains(idPedido.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Ordenação: 1º por Status (Aprovado → Pendente → Reprovado), 2º por Data (mais recente primeiro)
        pedidosAgrupados.sort((p1, p2) -> {
            // Definir prioridade dos status
            int prioridadeP1 = obterPrioridadeStatus(p1.getStatus());
            int prioridadeP2 = obterPrioridadeStatus(p2.getStatus());
            
            // Primeiro critério: Status
            int comparacaoStatus = Integer.compare(prioridadeP1, prioridadeP2);
            if (comparacaoStatus != 0) {
                return comparacaoStatus;
            }
            
            // Segundo critério: Data mais recente primeiro (ordem decrescente)
            LocalDate dataP1 = p1.getDataEmissaoFim(); // Usar data fim para pegar a mais recente do grupo
            LocalDate dataP2 = p2.getDataEmissaoFim();
            return dataP2.compareTo(dataP1); // Ordem decrescente (mais recente primeiro)
        });
        
        return pedidosAgrupados;
    }
    
    private Usuario obterUsuarioPadrao() {
        return usuarioRepository.findById(11L)
                .orElseThrow(() -> new RuntimeException("Usuário padrão (ID=11) não encontrado no banco de dados"));
    }
    
    private int obterPrioridadeStatus(String status) {
        if (status == null) return 999; // Status nulo vai para o final
        
        switch (status.toLowerCase()) {
            case "aprovado":
                return 1; // Primeira prioridade
            case "pendente":
                return 2; // Segunda prioridade  
            case "reprovado":
                return 3; // Terceira prioridade
            default:
                return 999; // Status desconhecido vai para o final
        }
    }
    
    private OrcamentoDTO convertToDTO(Orcamento orcamento) {
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setDataEmissao(orcamento.getDataEmissao());
        dto.setDataValidade(orcamento.getDataValidade());
        dto.setDataEntrega(orcamento.getDataEntrega());
        
        if (orcamento.getFornecedor() != null) {
            dto.setIdFornecedor(orcamento.getFornecedor().getId());
            dto.setNomeFornecedor(orcamento.getFornecedor().getDescricao());
        }
        
        if (orcamento.getProduto() != null) {
            dto.setIdProduto(orcamento.getProduto().getId());
            dto.setNomeProduto(orcamento.getProduto().getNome());
            dto.setDescricaoProduto(orcamento.getProduto().getDescricao());
        }
        
        if (orcamento.getUnidadeMedida() != null) {
            dto.setIdUnidadeMedida(orcamento.getUnidadeMedida().getId());
            dto.setUnidadeAbreviacao(orcamento.getUnidadeMedida().getUnidadeAbreviacao());
        }
        
        dto.setGarantia(orcamento.getGarantia());
        dto.setCondicoesPagamento(orcamento.getCondicoesPagamento());
        dto.setPrecoCompra(orcamento.getPrecoCompra());
        dto.setQuantidade(orcamento.getQuantidade());
        dto.setStatus(orcamento.getStatus());
        dto.setDataGeracao(orcamento.getDataGeracao());
        
        if (orcamento.getUsuarioAprovador() != null) {
            dto.setIdUserApprove(orcamento.getUsuarioAprovador().getIdUsuario());
        }
        
        return dto;
    }

    /**
     * Gera PDF para um pedido agrupado com múltiplos orçamentos
     */
    public byte[] gerarPdfPedidoAgrupado(String idPedido) {
        // Buscar o pedido agrupado específico
        List<PedidoAgrupadoDTO> pedidos = consultarPedidosAgrupados(null, null, null, null, idPedido, null);
        
        if (pedidos.isEmpty()) {
            return new byte[0];
        }
        
        PedidoAgrupadoDTO pedido = pedidos.get(0);
        
        // Buscar todos os orçamentos do pedido
        List<Orcamento> orcamentos = orcamentoRepository.findAllById(pedido.getIdOrcamentos());
        
        if (orcamentos.isEmpty()) {
            return new byte[0];
        }
        
        return pdfGenerationService.gerarPdfPedidoAgrupado(pedido, orcamentos);
    }

    /**
     * Gera ZIP com PDFs de múltiplos pedidos agrupados
     */
    public byte[] gerarZipPedidosAgrupados(List<String> idsPedidos) {
        if (idsPedidos == null || idsPedidos.isEmpty()) {
            return new byte[0];
        }

        Map<String, byte[]> pdfs = new HashMap<>();
        
        for (String idPedido : idsPedidos) {
            try {
                byte[] pdfBytes = gerarPdfPedidoAgrupado(idPedido);
                if (pdfBytes.length > 0) {
                    pdfs.put("Pedido_" + idPedido + ".pdf", pdfBytes);
                }
            } catch (Exception e) {
                // Pular pedidos com erro e continuar com os outros
                System.err.println("Erro ao gerar PDF para pedido " + idPedido + ": " + e.getMessage());
            }
        }
        
        return pdfGenerationService.gerarZipComPdfs(pdfs);
    }

    // Método público para obter a data/hora de aprovação
    public static LocalDateTime getDataHoraAprovacao(Long orcamentoId) {
        return datasHoraAprovacao.get(orcamentoId);
    }
}

