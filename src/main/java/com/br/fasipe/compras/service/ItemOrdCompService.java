package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Item_OrdComp;
import com.br.fasipe.compras.repository.ItemOrdCompRepository;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import com.br.fasipe.compras.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ItemOrdCompService {
    
    @Autowired
    private ItemOrdCompRepository itemOrdCompRepository;
    
    @Autowired
    private OrdemCompraRepository ordemCompraRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<Item_OrdComp> findAll() {
        return itemOrdCompRepository.findAll();
    }
    
    public Optional<Item_OrdComp> findById(Integer id) {
        return itemOrdCompRepository.findById(id);
    }
    
    public List<Item_OrdComp> findByOrdemCompraId(Integer ordemCompraId) {
        return itemOrdCompRepository.findByOrdemCompraId(ordemCompraId);
    }
    
    public List<Item_OrdComp> findByProdutoId(Integer produtoId) {
        return itemOrdCompRepository.findByProdutoId(produtoId);
    }
    
    public List<Item_OrdComp> findByDataVencimentoAnterior(LocalDate dataVencimento) {
        return itemOrdCompRepository.findByDataVencimentoBefore(dataVencimento);
    }
    
    public Item_OrdComp save(Item_OrdComp itemOrdComp) {
        validateItemOrdComp(itemOrdComp);
        return itemOrdCompRepository.save(itemOrdComp);
    }
    
    public Item_OrdComp update(Integer id, Item_OrdComp itemOrdComp) {
        Optional<Item_OrdComp> existingItem = itemOrdCompRepository.findById(id);
        if (existingItem.isPresent()) {
            itemOrdComp.setId(id);
            validateItemOrdComp(itemOrdComp);
            return itemOrdCompRepository.save(itemOrdComp);
        }
        throw new RuntimeException("Item da ordem de compra não encontrado com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (itemOrdCompRepository.existsById(id)) {
            itemOrdCompRepository.deleteById(id);
        } else {
            throw new RuntimeException("Item da ordem de compra não encontrado com ID: " + id);
        }
    }
    
    public Integer getTotalQuantidadeByOrdemCompra(Integer ordemCompraId) {
        Integer total = itemOrdCompRepository.getTotalQuantidadeByOrdemCompra(ordemCompraId);
        return total != null ? total : 0;
    }
    
    public List<Item_OrdComp> findItensVencendo(int diasAntecedencia) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasAntecedencia);
        return itemOrdCompRepository.findByDataVencimentoBefore(dataLimite);
    }
    
    private void validateItemOrdComp(Item_OrdComp itemOrdComp) {
        if (itemOrdComp.getOrdemCompra() == null || itemOrdComp.getOrdemCompra().getId() == null) {
            throw new IllegalArgumentException("Ordem de compra é obrigatória");
        }
        
        if (itemOrdComp.getProduto() == null || itemOrdComp.getProduto().getId() == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        
        if (itemOrdComp.getQuantidade() == null || itemOrdComp.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        if (itemOrdComp.getValor() == null || itemOrdComp.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        
        if (itemOrdComp.getDataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
        
        // Verificar se a ordem de compra existe
        if (!ordemCompraRepository.existsById(itemOrdComp.getOrdemCompra().getId())) {
            throw new IllegalArgumentException("Ordem de compra não encontrada");
        }
        
        // Verificar se o produto existe
        if (!produtoRepository.existsById(itemOrdComp.getProduto().getId())) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
    }
}
