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

    public byte[] gerarPdfsPorIds(List<Long> orcamentoIds) {
        if (orcamentoIds == null || orcamentoIds.isEmpty()) {
            return new byte[0];
        }
        
        List<Orcamento> orcamentosParaBaixar = orcamentoRepository.findAllById(orcamentoIds);

        List<Orcamento> orcamentosAprovados = orcamentosParaBaixar.stream()
                .filter(o -> "aprovado".equalsIgnoreCase(o.getStatus())) // Usando equalsIgnoreCase por segurança
                .collect(Collectors.toList());

        if (orcamentosAprovados.isEmpty()) {
            return new byte[0];
        }
        
        Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor = orcamentosAprovados.stream()
                .collect(Collectors.groupingBy(Orcamento::getFornecedor));
                
        return pdfGenerationService.gerarPdfsECompactar(orcamentosPorFornecedor, "Sistema de Compras");
    }

    public List<OrcamentoDTO> consultarOrdensDeCompra(LocalDate dataInicial, LocalDate dataFinal, 
                                                  String fornecedorNome, String produtoNome, Long idOrcamento, String status) {
        
        // ***** A CORREÇÃO ESTÁ AQUI *****
        // Se o status for uma string vazia ou a palavra "todos" (ignorando maiúsculas/minúsculas),
        // nós o transformamos em null.
        // A query no repositório já está preparada para ignorar o filtro quando o status é null.
        if (status != null && (status.trim().isEmpty() || status.equalsIgnoreCase("todos"))) {
            status = null;
        }

        List<Orcamento> orcamentos = orcamentoRepository.findWithFilters(dataInicial, dataFinal, fornecedorNome, produtoNome, idOrcamento, status);
        
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

