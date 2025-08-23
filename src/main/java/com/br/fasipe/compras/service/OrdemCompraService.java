package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.ItemOrdemCompra;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.model.OrdemCompra.StatusOrdemCompra;
import com.br.fasipe.compras.repository.ItemOrdemCompraRepository;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrdemCompraService {

    @Autowired
    private OrdemCompraRepository ordemCompraRepository;
    
    @Autowired
    private ItemOrdemCompraRepository itemOrdemCompraRepository;
    
    public List<OrdemCompra> listarTodas() {
        return ordemCompraRepository.findAll();
    }
    
    public Optional<OrdemCompra> buscarPorId(Integer id) {
        return ordemCompraRepository.findById(id);
    }
    
    public List<OrdemCompra> buscarPorStatus(StatusOrdemCompra status) {
        return ordemCompraRepository.findByStatus(status);
    }
    
    public List<OrdemCompra> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return ordemCompraRepository.findByDataOrdemBetween(inicio, fim);
    }
    
    public List<OrdemCompra> buscarPorDataPrevistaVencida(LocalDate dataAtual) {
        return ordemCompraRepository.findByDataPrevistaLessThanEqual(dataAtual);
    }
    
    @Transactional
    public OrdemCompra salvar(OrdemCompra ordemCompra) {
        // Validações de negócio podem ser adicionadas aqui
        if (ordemCompra.getDataOrdem() == null) {
            ordemCompra.setDataOrdem(LocalDate.now());
        }
        
        if (ordemCompra.getStatus() == null) {
            ordemCompra.setStatus(OrdemCompra.StatusOrdemCompra.PEND);
        }
        
        return ordemCompraRepository.save(ordemCompra);
    }
    
    @Transactional
    public OrdemCompra atualizarStatus(Integer id, StatusOrdemCompra novoStatus) {
        Optional<OrdemCompra> ordemCompraOpt = ordemCompraRepository.findById(id);
        
        if (ordemCompraOpt.isPresent()) {
            OrdemCompra ordemCompra = ordemCompraOpt.get();
            ordemCompra.setStatus(novoStatus);
            
            // Se o status for CONC (concluído), atualiza a data de entrega
            if (novoStatus == OrdemCompra.StatusOrdemCompra.CONC && ordemCompra.getDataEntrega() == null) {
                ordemCompra.setDataEntrega(LocalDate.now());
            }
            
            return ordemCompraRepository.save(ordemCompra);
        }
        
        return null;
    }
    
    @Transactional
    public void excluir(Integer id) {
        // Verificar se existem itens associados
        Optional<OrdemCompra> ordemCompraOpt = ordemCompraRepository.findById(id);
        
        if (ordemCompraOpt.isPresent()) {
            OrdemCompra ordemCompra = ordemCompraOpt.get();
            
            // Remover itens associados
            List<ItemOrdemCompra> itens = itemOrdemCompraRepository.findByOrdemCompra(ordemCompra);
            itemOrdemCompraRepository.deleteAll(itens);
            
            // Remover a ordem de compra
            ordemCompraRepository.delete(ordemCompra);
        }
    }
    
    @Transactional
    public void calcularValorTotal(Integer id) {
        Optional<OrdemCompra> ordemCompraOpt = ordemCompraRepository.findById(id);
        
        if (ordemCompraOpt.isPresent()) {
            OrdemCompra ordemCompra = ordemCompraOpt.get();
            List<ItemOrdemCompra> itens = itemOrdemCompraRepository.findByOrdemCompra(ordemCompra);
            
            BigDecimal valorTotal = itens.stream()
                .map(item -> item.getValor().multiply(new BigDecimal(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            ordemCompra.setValor(valorTotal);
            ordemCompraRepository.save(ordemCompra);
        }
    }
}
