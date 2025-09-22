package com.br.fasipe.compras.service;

import com.br.fasipe.compras.dto.OrcamentoDTO;
import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.Orcamento;
import com.br.fasipe.compras.model.Usuario;
import com.br.fasipe.compras.repository.OrcamentoRepository;
import com.br.fasipe.compras.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PdfGenerationService pdfGenerationService;

    public List<OrcamentoDTO> buscarOrcamentosPendentes() {
        // Agora este método vai funcionar, pois a query no repositório está correta
        List<Orcamento> orcamentos = orcamentoRepository.findByStatusPendente();
        return orcamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public byte[] processarEGerarOrdens(List<Long> orcamentoIdsAprovados) {
        // Obter usuário padrão para auditoria
        Usuario usuarioPadrao = obterUsuarioPadrao();
        
        List<Orcamento> orcamentosAprovados = orcamentoRepository.findAllById(orcamentoIdsAprovados);
        
        if (orcamentosAprovados.isEmpty()) {
            return new byte[0]; // Retorna um array vazio se nenhum ID válido for encontrado
        }

        Set<Long> produtoIds = orcamentosAprovados.stream()
                .map(o -> o.getProduto().getId()) // Corrigido para Long
                .map(Integer::longValue)                
                .collect(Collectors.toSet());
        
        // Reprovar concorrentes (assumindo que o método existe no repositório)
        if (!produtoIds.isEmpty()) {
             orcamentoRepository.reprovarConcorrentes(produtoIds, orcamentoIdsAprovados);
        }
        
        LocalDate agora = LocalDate.now();
        for (Orcamento orcamento : orcamentosAprovados) {
            orcamento.setStatus("aprovado"); // Use 'aprovado', minúsculo, para consistência com o ENUM
            orcamento.setDataGeracao(agora);
            orcamento.setUsuarioAprovador(usuarioPadrao); // O nome do método pode variar (ex: setIdUserAprove)
        }
        
        orcamentoRepository.saveAll(orcamentosAprovados);
        
        Map<Fornecedor, List<Orcamento>> orcamentosPorFornecedor = orcamentosAprovados.stream()
                .collect(Collectors.groupingBy(Orcamento::getFornecedor));
        
        // Assumindo que o nome do método no serviço de PDF é este
        return pdfGenerationService.gerarPdfsECompactar(orcamentosPorFornecedor, "Sistema de Compras");
    }

    // Este método de consulta parece complexo, mas está funcionalmente ok
    public List<OrcamentoDTO> consultarOrdensDeCompra(LocalDate dataInicial, LocalDate dataFinal, 
                                                      Long fornecedorId, Long produtoId, Long idOrcamento) { // Tipos ajustados para Long
        List<Orcamento> orcamentos = orcamentoRepository.findWithFilters(dataInicial, dataFinal, fornecedorId, produtoId, idOrcamento);
        return orcamentos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private Usuario obterUsuarioPadrao() {
        // CORREÇÃO AQUI: Alterado ID de 1 para 11, que existe no seu banco.
        return usuarioRepository.findById(11L)
                .orElseThrow(() -> new RuntimeException("Usuário padrão (ID=11) não encontrado no banco de dados"));
    }
    
    // O método convertToDTO parece ok, desde que os getters e setters estejam corretos nos seus modelos
   
private OrcamentoDTO convertToDTO(Orcamento orcamento) {
    OrcamentoDTO dto = new OrcamentoDTO();
    dto.setIdOrcamento(orcamento.getIdOrcamento());
    dto.setDataEmissao(orcamento.getDataEmissao());
    dto.setDataValidade(orcamento.getDataValidade());
    dto.setDataEntrega(orcamento.getDataEntrega());

    // Dados do fornecedor
    if (orcamento.getFornecedor() != null) {
        dto.setIdFornecedor(orcamento.getFornecedor().getId());
        dto.setNomeFornecedor(orcamento.getFornecedor().getDescricao()); // Corrigido
        dto.setRepresentante(orcamento.getFornecedor().getRepresentante());   // Corrigido
        dto.setContatoRepresentante(orcamento.getFornecedor().getContatoRepresentante()); // Corrigido
        dto.setDescricaoFornecedor(orcamento.getFornecedor().getDescricao()); // Corrigido
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
        dto.setUnidadeAbreviacao(orcamento.getUnidadeMedida().getUnidadeAbreviacao()); // Corrigido
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
        dto.setNomeUsuarioAprovador(orcamento.getUsuarioAprovador().getLoginUsuario()); // Corrigido
    }

    dto.setStatus(orcamento.getStatus());
    dto.setDataGeracao(orcamento.getDataGeracao());

    return dto;
}
}