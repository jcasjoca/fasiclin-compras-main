package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.model.Movimentacao.TipoMovimentacao;
import com.br.fasipe.compras.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    
    public List<Movimentacao> listarTodas() {
        return movimentacaoRepository.findAll();
    }
    
    public Optional<Movimentacao> buscarPorId(Integer id) {
        return movimentacaoRepository.findById(id);
    }
    
    public List<Movimentacao> buscarPorEstoque(Estoque estoque) {
        return movimentacaoRepository.findByEstoque(estoque);
    }
    
    public List<Movimentacao> buscarPorTipo(TipoMovimentacao tipoMovimentacao) {
        return movimentacaoRepository.findByTipoMovimentacao(tipoMovimentacao);
    }
    
    public List<Movimentacao> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoRepository.findByDataMovimentacaoBetween(inicio, fim);
    }
    
    public List<Movimentacao> buscarPorSetorOrigem(Integer idSetorOrigem) {
        return movimentacaoRepository.findByIdSetorOrigem(idSetorOrigem);
    }
    
    public List<Movimentacao> buscarPorSetorDestino(Integer idSetorDestino) {
        return movimentacaoRepository.findByIdSetorDestino(idSetorDestino);
    }
    
    public Movimentacao salvar(Movimentacao movimentacao) {
        // Validações de negócio podem ser adicionadas aqui
        if (movimentacao.getDataMovimentacao() == null) {
            movimentacao.setDataMovimentacao(LocalDate.now());
        }
        
        return movimentacaoRepository.save(movimentacao);
    }
    
    public void excluir(Integer id) {
        Optional<Movimentacao> movimentacaoOpt = movimentacaoRepository.findById(id);
        
        if (movimentacaoOpt.isPresent()) {
            Movimentacao movimentacao = movimentacaoOpt.get();
            movimentacaoRepository.delete(movimentacao);
        }
    }
}
