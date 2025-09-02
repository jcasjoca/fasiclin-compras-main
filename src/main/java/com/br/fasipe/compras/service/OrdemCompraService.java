package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrdemCompraService {
    
    @Autowired
    private OrdemCompraRepository ordemCompraRepository;
    
    public List<OrdemCompra> findAll() {
        return ordemCompraRepository.findAll();
    }
    
    public Optional<OrdemCompra> findById(Integer id) {
        return ordemCompraRepository.findById(id);
    }
    
    public List<OrdemCompra> findByStatus(OrdemCompra.StatusOrdemCompra status) {
        return ordemCompraRepository.findByStatus(status);
    }
    
    public List<OrdemCompra> findByPeriodoOrdem(LocalDate dataInicio, LocalDate dataFim) {
        return ordemCompraRepository.findByDataOrdemBetween(dataInicio, dataFim);
    }
    
    public List<OrdemCompra> findByPeriodoPrevisto(LocalDate dataInicio, LocalDate dataFim) {
        return ordemCompraRepository.findByDataPrevistaBetween(dataInicio, dataFim);
    }
    
    public List<OrdemCompra> findByPeriodoEntrega(LocalDate dataInicio, LocalDate dataFim) {
        return ordemCompraRepository.findByDataEntregaBetween(dataInicio, dataFim);
    }
    
    public List<OrdemCompra> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return ordemCompraRepository.findByValorBetween(valorMinimo, valorMaximo);
    }
    
    public OrdemCompra save(OrdemCompra ordemCompra) {
        validateOrdemCompra(ordemCompra);
        if (ordemCompra.getDataOrdem() == null) {
            ordemCompra.setDataOrdem(LocalDate.now());
        }
        if (ordemCompra.getStatus() == null) {
            ordemCompra.setStatus(OrdemCompra.StatusOrdemCompra.PEND);
        }
        return ordemCompraRepository.save(ordemCompra);
    }
    
    public OrdemCompra update(Integer id, OrdemCompra ordemCompra) {
        Optional<OrdemCompra> existingOrdem = ordemCompraRepository.findById(id);
        if (existingOrdem.isPresent()) {
            ordemCompra.setId(id);
            validateOrdemCompra(ordemCompra);
            return ordemCompraRepository.save(ordemCompra);
        }
        throw new RuntimeException("Ordem de compra não encontrada com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (ordemCompraRepository.existsById(id)) {
            ordemCompraRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ordem de compra não encontrada com ID: " + id);
        }
    }
    
    public List<OrdemCompra> findOrdensAtrasadas() {
        return ordemCompraRepository.findOrdensAtrasadas();
    }
    
    public List<OrdemCompra> findOrdensEmAndamentoSemEntrega() {
        return ordemCompraRepository.findOrdensEmAndamentoSemEntrega();
    }
    
    public BigDecimal getValorMedioOrdensConcluidas() {
        BigDecimal valorMedio = ordemCompraRepository.getValorMedioOrdensConcluidas();
        return valorMedio != null ? valorMedio : BigDecimal.ZERO;
    }
    
    public Long countByStatus(OrdemCompra.StatusOrdemCompra status) {
        return ordemCompraRepository.countByStatus(status);
    }
    
    public OrdemCompra atualizarStatus(Integer id, OrdemCompra.StatusOrdemCompra novoStatus) {
        Optional<OrdemCompra> ordemOptional = ordemCompraRepository.findById(id);
        if (ordemOptional.isPresent()) {
            OrdemCompra ordem = ordemOptional.get();
            ordem.setStatus(novoStatus);
            
            // Se for concluída, atualizar data de entrega se não estiver preenchida
            if (novoStatus == OrdemCompra.StatusOrdemCompra.CONC && ordem.getDataEntrega() == null) {
                ordem.setDataEntrega(LocalDate.now());
            }
            
            return ordemCompraRepository.save(ordem);
        }
        throw new RuntimeException("Ordem de compra não encontrada com ID: " + id);
    }
    
    public List<OrdemCompra> findOrdensPendentes() {
        return findByStatus(OrdemCompra.StatusOrdemCompra.PEND);
    }
    
    public List<OrdemCompra> findOrdensEmAndamento() {
        return findByStatus(OrdemCompra.StatusOrdemCompra.ANDA);
    }
    
    public List<OrdemCompra> findOrdensConcluidas() {
        return findByStatus(OrdemCompra.StatusOrdemCompra.CONC);
    }
    
    private void validateOrdemCompra(OrdemCompra ordemCompra) {
        if (ordemCompra.getValor() == null || ordemCompra.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        
        if (ordemCompra.getDataPrevista() == null) {
            throw new IllegalArgumentException("Data prevista é obrigatória");
        }
        
        if (ordemCompra.getDataOrdem() == null) {
            throw new IllegalArgumentException("Data da ordem é obrigatória");
        }
        
        // Validar se data prevista não é anterior à data da ordem
        if (ordemCompra.getDataPrevista().isBefore(ordemCompra.getDataOrdem())) {
            throw new IllegalArgumentException("Data prevista não pode ser anterior à data da ordem");
        }
        
        // Se data de entrega foi informada, validar se não é anterior à data da ordem
        if (ordemCompra.getDataEntrega() != null && ordemCompra.getDataEntrega().isBefore(ordemCompra.getDataOrdem())) {
            throw new IllegalArgumentException("Data de entrega não pode ser anterior à data da ordem");
        }
    }
}
