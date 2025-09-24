package com.br.fasipe.compras.service;

import com.br.fasipe.compras.dto.OrcamentoDTO;
import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import com.br.fasipe.compras.model.Usuario;
import com.br.fasipe.compras.repository.OrcamentoRepository;
import com.br.fasipe.compras.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdemDeCompraService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PdfGenerationService pdfGenerationService;

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
        for (Orcamento orcamento : orcamentosAprovados) {
            orcamento.setStatus("aprovado");
            orcamento.setDataGeracao(agora);
            orcamento.setUsuarioAprovador(usuarioPadrao);
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

        if ("aprovado".equals(status) || "reprovado".equals(status)) {
            return pdfGenerationService.gerarPdfUnico(orcamento);
        }
        
        return new byte[0];
    }
    
    public byte[] gerarPdfsPorIds(List<Long> orcamentoIds) {
        if (orcamentoIds == null || orcamentoIds.isEmpty()) {
            return new byte[0];
        }
        
        List<Orcamento> orcamentosParaBaixar = orcamentoRepository.findAllById(orcamentoIds);

        List<Orcamento> orcamentosValidos = orcamentosParaBaixar.stream()
                .filter(o -> "aprovado".equalsIgnoreCase(o.getStatus()) || "reprovado".equalsIgnoreCase(o.getStatus()))
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
    
    private Usuario obterUsuarioPadrao() {
        return usuarioRepository.findById(11L)
                .orElseThrow(() -> new RuntimeException("Usuário padrão (ID=11) não encontrado no banco de dados"));
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
}

