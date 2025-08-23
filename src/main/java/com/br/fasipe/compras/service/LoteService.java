package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.repository.EstoqueRepository;
import com.br.fasipe.compras.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoteService {

    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private EstoqueRepository estoqueRepository;
    
    public List<Lote> listarTodos() {
        return loteRepository.findAll();
    }
    
    public Optional<Lote> buscarPorId(Integer id) {
        return loteRepository.findById(id);
    }
    
    public List<Lote> buscarPorOrdemCompra(OrdemCompra ordemCompra) {
        return loteRepository.findByOrdemCompra(ordemCompra);
    }
    
    public List<Lote> buscarPorVencimento(LocalDate dataLimite) {
        return loteRepository.findByDataVencimentoBefore(dataLimite);
    }
    
    @Transactional
    public Lote salvar(Lote lote) {
        // Validações de negócio podem ser adicionadas aqui
        return loteRepository.save(lote);
    }
    
    @Transactional
    public void excluir(Integer id) {
        // Verificar se existem estoques associados
        Optional<Lote> loteOpt = loteRepository.findById(id);
        
        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            
            // Verificar se há estoque associado ao lote
            List<Estoque> estoques = estoqueRepository.findByLote(lote);
            
            if (!estoques.isEmpty()) {
                throw new IllegalStateException("Não é possível excluir o lote pois existem registros de estoque associados");
            }
            
            // Remover o lote
            loteRepository.delete(lote);
        }
    }
    
    public List<Lote> buscarLotesAVencer(int diasParaVencimento) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasParaVencimento);
        return loteRepository.findByDataVencimentoBefore(dataLimite);
    }
    
    @Transactional
    public void atualizarQuantidade(Integer id, Integer novaQuantidade) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa");
        }
        
        Optional<Lote> loteOpt = loteRepository.findById(id);
        
        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            lote.setQuantidade(novaQuantidade);
            loteRepository.save(lote);
        }
    }
}
