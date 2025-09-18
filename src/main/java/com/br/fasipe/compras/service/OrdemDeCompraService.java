package com.br.fasipe.compras.service;

import com.br.fasipe.compras.dto.OrcamentoDTO;
import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.GrupoAprovador;
import com.br.fasipe.compras.model.Orcamento;
import com.br.fasipe.compras.model.Usuario;
import com.br.fasipe.compras.repository.GrupoAprovadorRepository;
import com.br.fasipe.compras.repository.OrcamentoRepository;
import com.br.fasipe.compras.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdemDeCompraService {
    
    @Autowired
    private OrcamentoRepository orcamentoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private GrupoAprovadorRepository grupoAprovadorRepository;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    public List<OrcamentoDTO> buscarOrcamentosPendentes() {
        List<Orcamento> orcamentos = orcamentoRepository.findByStatusPendente();
        return orcamentos.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public byte[] processarEGerarOrdens(List<Long> orcamentoIdsAprovados) {
        // Obter usuário logado
        Usuario usuarioLogado = obterUsuarioLogado();
        
        // Buscar orçamentos aprovados
        List<Orcamento> orcamentosAprovados = orcamentoRepository.findAllById(orcamentoIdsAprovados);
        
        // Verificar se o usuário tem permissão para aprovar todos os orçamentos
        verificarPermissaoAprovacao(usuarioLogado, orcamentosAprovados);
        
        // Extrair IDs únicos dos produtos
        Set<Integer> produtoIds = orcamentosAprovados.stream()
            .map(o -> o.getProduto().getId())
            .collect(Collectors.toSet());
        
        // Reprovar concorrentes
        orcamentoRepository.reprovarConcorrentes(produtoIds, orcamentoIdsAprovados);
        
        // Atualizar orçamentos aprovados
        LocalDate agora = LocalDate.now();
        for (Orcamento orcamento : orcamentosAprovados) {
            orcamento.setStatus("aprovado");
            orcamento.setDataGeracao(agora);
            orcamento.setUsuarioAprovador(usuarioLogado);
        }
        
        // Salvar alterações
        orcamentoRepository.saveAll(orcamentosAprovados);
        
        // Agrupar por fornecedor
        Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor = orcamentosAprovados.stream()
            .collect(Collectors.groupingBy(Orcamento::getFornecedor));
        
        // Gerar PDFs e compactar
        return pdfGenerationService.gerarPdfsECompactar(orcamentosPorFornecedor, usuarioLogado.getLoginUsuario());
    }
    
    public List<OrcamentoDTO> consultarOrdensDeCompra(LocalDate dataInicial, LocalDate dataFinal, 
                                                     Integer fornecedorId, Integer produtoId, Long idOrcamento) {
        List<Orcamento> orcamentos;
        
        // Se foi especificado um ID específico, buscar apenas por ele
        if (idOrcamento != null) {
            Optional<Orcamento> orcamentoOpt = orcamentoRepository.findById(idOrcamento);
            if (orcamentoOpt.isPresent() && "aprovado".equals(orcamentoOpt.get().getStatus())) {
                Orcamento orcamento = orcamentoOpt.get();
                // Verificar se está dentro da faixa de datas
                if (orcamento.getDataGeracao() != null && 
                    !orcamento.getDataGeracao().isBefore(dataInicial) && 
                    !orcamento.getDataGeracao().isAfter(dataFinal)) {
                    orcamentos = List.of(orcamento);
                } else {
                    orcamentos = List.of(); // Data fora do período
                }
            } else {
                orcamentos = List.of(); // ID não encontrado ou não aprovado
            }
        } else if (fornecedorId != null && produtoId != null) {
            orcamentos = orcamentoRepository.findByStatusAprovadoAndDataGeracaoBetweenAndFornecedorAndProduto(
                dataInicial, dataFinal, fornecedorId, produtoId);
        } else if (fornecedorId != null) {
            orcamentos = orcamentoRepository.findByStatusAprovadoAndDataGeracaoBetweenAndFornecedor(
                dataInicial, dataFinal, fornecedorId);
        } else if (produtoId != null) {
            orcamentos = orcamentoRepository.findByStatusAprovadoAndDataGeracaoBetweenAndProduto(
                dataInicial, dataFinal, produtoId);
        } else {
            orcamentos = orcamentoRepository.findByStatusAprovadoAndDataGeracaoBetween(dataInicial, dataFinal);
        }
        
        return orcamentos.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private Usuario obterUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return usuarioRepository.findByLoginUsuario(login)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + login));
    }
    
    private void verificarPermissaoAprovacao(Usuario usuario, List<Orcamento> orcamentos) {
        List<GrupoAprovador> gruposDoUsuario = grupoAprovadorRepository.findByUsuarioIdAndStatusAtivo(usuario.getIdUsuario());
        Set<Long> gruposPermitidos = gruposDoUsuario.stream()
            .map(GrupoAprovador::getIdGrupoAprovador)
            .collect(Collectors.toSet());
        
        for (Orcamento orcamento : orcamentos) {
            if (!gruposPermitidos.contains(orcamento.getGrupoAprovador().getIdGrupoAprovador())) {
                throw new RuntimeException("Usuário não tem permissão para aprovar o orçamento ID: " + orcamento.getIdOrcamento());
            }
        }
    }
    
    private OrcamentoDTO convertToDTO(Orcamento orcamento) {
        OrcamentoDTO dto = new OrcamentoDTO();
        dto.setIdOrcamento(orcamento.getIdOrcamento());
        dto.setDataEmissao(orcamento.getDataEmissao());
        dto.setDataValidade(orcamento.getDataValidade());
        dto.setDataEntrega(orcamento.getDataEntrega());
        
        // Dados do fornecedor
        if (orcamento.getFornecedor() != null) {
            dto.setIdFornecedor(orcamento.getFornecedor().getId());
            dto.setNomeFornecedor(orcamento.getFornecedor().getDescricao());
            dto.setRepresentante(orcamento.getFornecedor().getRepresentante());
            dto.setContatoRepresentante(orcamento.getFornecedor().getContatoRepresentante());
            dto.setDescricaoFornecedor(orcamento.getFornecedor().getDescricao());
        }
        
        // Dados do produto
        if (orcamento.getProduto() != null) {
            dto.setIdProduto(orcamento.getProduto().getId());
            dto.setNomeProduto(orcamento.getProduto().getNome());
            dto.setDescricaoProduto(orcamento.getProduto().getDescricao());
            dto.setCodigoBarras(orcamento.getProduto().getCodigoBarras());
        }
        
        // Dados da unidade de medida
        if (orcamento.getUnidadeMedida() != null) {
            dto.setIdUnidadeMedida(orcamento.getUnidadeMedida().getId());
            dto.setDescricaoUnidadeMedida(orcamento.getUnidadeMedida().getDescricao());
            dto.setUnidadeAbreviacao(orcamento.getUnidadeMedida().getUnidadeAbreviacao());
        }
        
        dto.setGarantia(orcamento.getGarantia());
        dto.setCondicoesPagamento(orcamento.getCondicoesPagamento());
        dto.setPrecoCompra(orcamento.getPrecoCompra());
        dto.setQuantidade(orcamento.getQuantidade());
        
        if (orcamento.getGrupoAprovador() != null) {
            dto.setIdGrupoAprovador(orcamento.getGrupoAprovador().getIdGrupoAprovador());
        }
        
        if (orcamento.getUsuarioAprovador() != null) {
            dto.setIdUserApprove(orcamento.getUsuarioAprovador().getIdUsuario());
            dto.setNomeUsuarioAprovador(orcamento.getUsuarioAprovador().getLoginUsuario());
        }
        
        dto.setStatus(orcamento.getStatus());
        dto.setDataGeracao(orcamento.getDataGeracao());
        
        return dto;
    }
}