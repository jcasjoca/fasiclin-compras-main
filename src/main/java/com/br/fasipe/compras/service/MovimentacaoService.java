package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {
    
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    
    public List<Movimentacao> findAll() {
        return movimentacaoRepository.findAll();
    }
    
    public Optional<Movimentacao> findById(Integer id) {
        return movimentacaoRepository.findById(id);
    }
    
    public List<Movimentacao> findByIdEstoque(Integer idEstoque) {
        return movimentacaoRepository.findByIdEstoque(idEstoque);
    }
    
    public List<Movimentacao> findByIdUsuario(Integer idUsuario) {
        return movimentacaoRepository.findByIdUsuario(idUsuario);
    }
    
    public List<Movimentacao> findByTipoMovimentacao(Movimentacao.TipoMovimentacao tipoMovimentacao) {
        return movimentacaoRepository.findByTipoMovimentacao(tipoMovimentacao);
    }
    
    public List<Movimentacao> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return movimentacaoRepository.findByDataMovimentacaoBetween(dataInicio, dataFim);
    }
    
    public List<Movimentacao> findBySetorOrigem(Integer idSetorOrigem) {
        return movimentacaoRepository.findByIdSetorOrigem(idSetorOrigem);
    }
    
    public List<Movimentacao> findBySetorDestino(Integer idSetorDestino) {
        return movimentacaoRepository.findByIdSetorDestino(idSetorDestino);
    }
    
    public Movimentacao save(Movimentacao movimentacao) {
        validateMovimentacao(movimentacao);
        if (movimentacao.getDataMovimentacao() == null) {
            movimentacao.setDataMovimentacao(LocalDate.now());
        }
        return movimentacaoRepository.save(movimentacao);
    }
    
    public Movimentacao update(Integer id, Movimentacao movimentacao) {
        Optional<Movimentacao> existingMovimentacao = movimentacaoRepository.findById(id);
        if (existingMovimentacao.isPresent()) {
            movimentacao.setId(id);
            validateMovimentacao(movimentacao);
            return movimentacaoRepository.save(movimentacao);
        }
        throw new RuntimeException("Movimentação não encontrada com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (movimentacaoRepository.existsById(id)) {
            movimentacaoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Movimentação não encontrada com ID: " + id);
        }
    }
    
    public List<Movimentacao> findByEstoqueAndTipo(Integer idEstoque, Movimentacao.TipoMovimentacao tipoMovimentacao) {
        return movimentacaoRepository.findByEstoqueAndTipo(idEstoque, tipoMovimentacao);
    }
    
    public Integer getTotalEntradasByEstoque(Integer idEstoque) {
        Integer total = movimentacaoRepository.getTotalEntradasByEstoque(idEstoque);
        return total != null ? total : 0;
    }
    
    public Integer getTotalSaidasByEstoque(Integer idEstoque) {
        Integer total = movimentacaoRepository.getTotalSaidasByEstoque(idEstoque);
        return total != null ? total : 0;
    }
    
    public Integer getSaldoEstoque(Integer idEstoque) {
        return getTotalEntradasByEstoque(idEstoque) - getTotalSaidasByEstoque(idEstoque);
    }
    
    public List<Movimentacao> findMovimentacoesHoje() {
        return movimentacaoRepository.findByDataMovimentacaoBetween(LocalDate.now(), LocalDate.now());
    }
    
    private void validateMovimentacao(Movimentacao movimentacao) {
        if (movimentacao.getIdEstoque() == null) {
            throw new IllegalArgumentException("ID do estoque é obrigatório");
        }
        
        if (movimentacao.getIdUsuario() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
        
        if (movimentacao.getIdSetorOrigem() == null) {
            throw new IllegalArgumentException("ID do setor origem é obrigatório");
        }
        
        if (movimentacao.getIdSetorDestino() == null) {
            throw new IllegalArgumentException("ID do setor destino é obrigatório");
        }
        
        if (movimentacao.getQuantidadeMovimentacao() == null || movimentacao.getQuantidadeMovimentacao() <= 0) {
            throw new IllegalArgumentException("Quantidade de movimentação deve ser maior que zero");
        }
        
        if (movimentacao.getTipoMovimentacao() == null) {
            throw new IllegalArgumentException("Tipo de movimentação é obrigatório");
        }
        
        // Validar se setor origem e destino são diferentes
        if (movimentacao.getIdSetorOrigem().equals(movimentacao.getIdSetorDestino())) {
            throw new IllegalArgumentException("Setor origem e destino não podem ser iguais");
        }
    }
}
