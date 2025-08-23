package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.ItemOrdemCompra;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.ItemOrdemCompraRepository;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemOrdemCompraService {

    @Autowired
    private ItemOrdemCompraRepository itemOrdemCompraRepository;
    
    // OrdemCompraRepository não é necessário, removido para evitar warnings
    
    @Autowired
    private OrdemCompraService ordemCompraService;
    
    public List<ItemOrdemCompra> listarTodos() {
        return itemOrdemCompraRepository.findAll();
    }
    
    public Optional<ItemOrdemCompra> buscarPorId(Integer id) {
        return itemOrdemCompraRepository.findById(id);
    }
    
    public List<ItemOrdemCompra> buscarPorOrdemCompra(OrdemCompra ordemCompra) {
        return itemOrdemCompraRepository.findByOrdemCompra(ordemCompra);
    }
    
    public List<ItemOrdemCompra> buscarPorProduto(Produto produto) {
        return itemOrdemCompraRepository.findByProduto(produto);
    }
    
    @Transactional
    public ItemOrdemCompra salvar(ItemOrdemCompra itemOrdemCompra) {
        // Validações de negócio podem ser adicionadas aqui
        ItemOrdemCompra itemSalvo = itemOrdemCompraRepository.save(itemOrdemCompra);
        
        // Recalcular o valor total da ordem de compra
        if (itemOrdemCompra.getOrdemCompra() != null && itemOrdemCompra.getOrdemCompra().getId() != null) {
            ordemCompraService.calcularValorTotal(itemOrdemCompra.getOrdemCompra().getId());
        }
        
        return itemSalvo;
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<ItemOrdemCompra> itemOpt = itemOrdemCompraRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            ItemOrdemCompra item = itemOpt.get();
            Integer ordemCompraId = null;
            
            // Guardar o ID da ordem de compra para recalcular o valor total após a exclusão
            if (item.getOrdemCompra() != null) {
                ordemCompraId = item.getOrdemCompra().getId();
            }
            
            // Excluir o item
            itemOrdemCompraRepository.delete(item);
            
            // Recalcular o valor total da ordem de compra
            if (ordemCompraId != null) {
                ordemCompraService.calcularValorTotal(ordemCompraId);
            }
        }
    }
    
    @Transactional
    public void atualizarQuantidade(Integer id, Integer novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        
        Optional<ItemOrdemCompra> itemOpt = itemOrdemCompraRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            ItemOrdemCompra item = itemOpt.get();
            item.setQuantidade(novaQuantidade);
            itemOrdemCompraRepository.save(item);
            
            // Recalcular o valor total da ordem de compra
            if (item.getOrdemCompra() != null && item.getOrdemCompra().getId() != null) {
                ordemCompraService.calcularValorTotal(item.getOrdemCompra().getId());
            }
        }
    }
}
