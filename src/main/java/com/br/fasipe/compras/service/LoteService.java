package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.repository.LoteRepository;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoteService {
    
    @Autowired
    private LoteRepository loteRepository;
    
    @Autowired
    private OrdemCompraRepository ordemCompraRepository;
    
    public List<Lote> findAll() {
        return loteRepository.findAll();
    }
    
    public Optional<Lote> findById(Integer id) {
        return loteRepository.findById(id);
    }
    
    public List<Lote> findByOrdemCompraId(Integer ordemCompraId) {
        return loteRepository.findByOrdemCompraId(ordemCompraId);
    }
    
    public List<Lote> findByDataVencimentoAnterior(LocalDate dataVencimento) {
        return loteRepository.findByDataVencimentoBefore(dataVencimento);
    }
    
    public List<Lote> findByPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return loteRepository.findByDataVencimentoBetween(dataInicio, dataFim);
    }
    
    public Lote save(Lote lote) {
        validateLote(lote);
        return loteRepository.save(lote);
    }
    
    public Lote update(Integer id, Lote lote) {
        Optional<Lote> existingLote = loteRepository.findById(id);
        if (existingLote.isPresent()) {
            lote.setId(id);
            validateLote(lote);
            return loteRepository.save(lote);
        }
        throw new RuntimeException("Lote não encontrado com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (loteRepository.existsById(id)) {
            loteRepository.deleteById(id);
        } else {
            throw new RuntimeException("Lote não encontrado com ID: " + id);
        }
    }
    
    public Integer getTotalQuantidadeByOrdemCompra(Integer ordemCompraId) {
        Integer total = loteRepository.getTotalQuantidadeByOrdemCompra(ordemCompraId);
        return total != null ? total : 0;
    }
    
    public List<Lote> findLotesVencendoEm(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return loteRepository.findLotesVencendoAte(dataLimite);
    }
    
    public List<Lote> findLotesVencidos() {
        return loteRepository.findByDataVencimentoBefore(LocalDate.now());
    }
    
    public List<Lote> findLotesVencendoHoje() {
        return loteRepository.findByDataVencimentoBetween(LocalDate.now(), LocalDate.now());
    }
    
    private void validateLote(Lote lote) {
        if (lote.getOrdemCompra() == null || lote.getOrdemCompra().getId() == null) {
            throw new IllegalArgumentException("Ordem de compra é obrigatória");
        }
        
        if (lote.getDataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
        
        if (lote.getQuantidade() == null || lote.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        // Verificar se a ordem de compra existe
        if (!ordemCompraRepository.existsById(lote.getOrdemCompra().getId())) {
            throw new IllegalArgumentException("Ordem de compra não encontrada");
        }
        
        // Validar se a data de vencimento não é no passado (para novos lotes)
        if (lote.getId() == null && lote.getDataVencimento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento não pode ser no passado");
        }
    }
}
