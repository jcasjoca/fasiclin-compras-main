package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.ControlePedidos;
import com.br.fasipe.compras.repository.ControlePedidosRepository;
import com.br.fasipe.compras.repository.FornecedorRepository;
import com.br.fasipe.compras.repository.OrdemCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ControlePedidosService {
    
    @Autowired
    private ControlePedidosRepository controlePedidosRepository;
    
    @Autowired
    private FornecedorRepository fornecedorRepository;
    
    @Autowired
    private OrdemCompraRepository ordemCompraRepository;
    
    public List<ControlePedidos> findAll() {
        return controlePedidosRepository.findAll();
    }
    
    public Optional<ControlePedidos> findById(Integer id) {
        return controlePedidosRepository.findById(id);
    }
    
    public Optional<ControlePedidos> findByNumeroPedido(String numeroPedido) {
        return controlePedidosRepository.findByNumeroPedido(numeroPedido);
    }
    
    public List<ControlePedidos> findByFornecedorId(Integer fornecedorId) {
        return controlePedidosRepository.findByFornecedorId(fornecedorId);
    }
    
    public List<ControlePedidos> findByOrdemCompraId(Integer ordemCompraId) {
        return controlePedidosRepository.findByOrdemCompraId(ordemCompraId);
    }
    
    public List<ControlePedidos> findByStatusPedido(ControlePedidos.StatusPedido statusPedido) {
        return controlePedidosRepository.findByStatusPedido(statusPedido);
    }
    
    public List<ControlePedidos> findByTipoPagamento(ControlePedidos.TipoPagamento tipoPagamento) {
        return controlePedidosRepository.findByTipoPagamento(tipoPagamento);
    }
    
    public List<ControlePedidos> findByPeriodoPedido(LocalDate dataInicio, LocalDate dataFim) {
        return controlePedidosRepository.findByDataPedidoBetween(dataInicio, dataFim);
    }
    
    public List<ControlePedidos> findByPeriodoPrevistaEntrega(LocalDate dataInicio, LocalDate dataFim) {
        return controlePedidosRepository.findByDataPrevistaEntregaBetween(dataInicio, dataFim);
    }
    
    public List<ControlePedidos> findByPeriodoRealEntrega(LocalDate dataInicio, LocalDate dataFim) {
        return controlePedidosRepository.findByDataRealEntregaBetween(dataInicio, dataFim);
    }
    
    public List<ControlePedidos> findByValorRange(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return controlePedidosRepository.findByValorTotalBetween(valorMinimo, valorMaximo);
    }
    
    public Optional<ControlePedidos> findByNumeroNotaFiscal(String numeroNotaFiscal) {
        return controlePedidosRepository.findByNumeroNotaFiscal(numeroNotaFiscal);
    }
    
    public Optional<ControlePedidos> findByChaveNfe(String chaveNfe) {
        return controlePedidosRepository.findByChaveNfe(chaveNfe);
    }
    
    public List<ControlePedidos> findByUsuarioResponsavel(Integer idUsuarioResponsavel) {
        return controlePedidosRepository.findByIdUsuarioResponsavel(idUsuarioResponsavel);
    }
    
    public ControlePedidos save(ControlePedidos controlePedidos) {
        validateControlePedidos(controlePedidos);
        
        // Definir data de criação se não informada
        if (controlePedidos.getDataCriacao() == null) {
            controlePedidos.setDataCriacao(LocalDate.now());
        }
        
        // Definir data de pedido se não informada
        if (controlePedidos.getDataPedido() == null) {
            controlePedidos.setDataPedido(LocalDate.now());
        }
        
        // Definir status padrão se não informado
        if (controlePedidos.getStatusPedido() == null) {
            controlePedidos.setStatusPedido(ControlePedidos.StatusPedido.SOLICITADO);
        }
        
        return controlePedidosRepository.save(controlePedidos);
    }
    
    public ControlePedidos update(Integer id, ControlePedidos controlePedidos) {
        Optional<ControlePedidos> existingPedido = controlePedidosRepository.findById(id);
        if (existingPedido.isPresent()) {
            controlePedidos.setId(id);
            controlePedidos.setDataUltimaAtualizacao(LocalDate.now());
            validateControlePedidos(controlePedidos);
            return controlePedidosRepository.save(controlePedidos);
        }
        throw new RuntimeException("Controle de pedido não encontrado com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (controlePedidosRepository.existsById(id)) {
            controlePedidosRepository.deleteById(id);
        } else {
            throw new RuntimeException("Controle de pedido não encontrado com ID: " + id);
        }
    }
    
    public List<ControlePedidos> findPedidosAtrasados() {
        return controlePedidosRepository.findPedidosAtrasados();
    }
    
    public List<ControlePedidos> findPedidosVencendoHoje() {
        return controlePedidosRepository.findPedidosVencendoHoje();
    }
    
    public List<ControlePedidos> findPedidosVencendoEm(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return controlePedidosRepository.findPedidosVencendoAte(dataLimite);
    }
    
    public List<ControlePedidos> findPedidosEntreguesSemNotaFiscal() {
        return controlePedidosRepository.findPedidosEntreguesSemNotaFiscal();
    }
    
    public BigDecimal getSomaValorTotalByStatus(ControlePedidos.StatusPedido statusPedido) {
        BigDecimal soma = controlePedidosRepository.getSomaValorTotalByStatus(statusPedido);
        return soma != null ? soma : BigDecimal.ZERO;
    }
    
    public Long countByStatusPedido(ControlePedidos.StatusPedido statusPedido) {
        return controlePedidosRepository.countByStatusPedido(statusPedido);
    }
    
    public BigDecimal getValorMedioPedidosEntregues() {
        BigDecimal valorMedio = controlePedidosRepository.getValorMedioPedidosEntregues();
        return valorMedio != null ? valorMedio : BigDecimal.ZERO;
    }
    
    public List<ControlePedidos> findPedidosComValorMaiorQue(BigDecimal valorMinimo) {
        return controlePedidosRepository.findPedidosComValorMaiorQue(valorMinimo);
    }
    
    public List<ControlePedidos> findByCriacaoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return controlePedidosRepository.findByCriacaoBetween(dataInicio, dataFim);
    }
    
    public ControlePedidos atualizarStatus(Integer id, ControlePedidos.StatusPedido novoStatus) {
        Optional<ControlePedidos> pedidoOptional = controlePedidosRepository.findById(id);
        if (pedidoOptional.isPresent()) {
            ControlePedidos pedido = pedidoOptional.get();
            pedido.setStatusPedido(novoStatus);
            pedido.setDataUltimaAtualizacao(LocalDate.now());
            
            // Se for entregue, definir data real de entrega se não estiver preenchida
            if (novoStatus == ControlePedidos.StatusPedido.ENTREGUE && pedido.getDataRealEntrega() == null) {
                pedido.setDataRealEntrega(LocalDate.now());
            }
            
            return controlePedidosRepository.save(pedido);
        }
        throw new RuntimeException("Controle de pedido não encontrado com ID: " + id);
    }
    
    public ControlePedidos adicionarNotaFiscal(Integer id, String numeroNotaFiscal, String chaveNfe) {
        Optional<ControlePedidos> pedidoOptional = controlePedidosRepository.findById(id);
        if (pedidoOptional.isPresent()) {
            ControlePedidos pedido = pedidoOptional.get();
            pedido.setNumeroNotaFiscal(numeroNotaFiscal);
            pedido.setChaveNfe(chaveNfe);
            pedido.setDataUltimaAtualizacao(LocalDate.now());
            return controlePedidosRepository.save(pedido);
        }
        throw new RuntimeException("Controle de pedido não encontrado com ID: " + id);
    }
    
    private void validateControlePedidos(ControlePedidos controlePedidos) {
        if (controlePedidos.getNumeroPedido() == null || controlePedidos.getNumeroPedido().trim().isEmpty()) {
            throw new IllegalArgumentException("Número do pedido é obrigatório");
        }
        
        if (controlePedidos.getFornecedor() == null || controlePedidos.getFornecedor().getId() == null) {
            throw new IllegalArgumentException("Fornecedor é obrigatório");
        }
        
        if (controlePedidos.getOrdemCompra() == null || controlePedidos.getOrdemCompra().getId() == null) {
            throw new IllegalArgumentException("Ordem de compra é obrigatória");
        }
        
        if (controlePedidos.getDataPrevistaEntrega() == null) {
            throw new IllegalArgumentException("Data prevista de entrega é obrigatória");
        }
        
        if (controlePedidos.getValorTotal() == null || controlePedidos.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero");
        }
        
        if (controlePedidos.getTipoPagamento() == null) {
            throw new IllegalArgumentException("Tipo de pagamento é obrigatório");
        }
        
        if (controlePedidos.getIdUsuarioResponsavel() == null) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório");
        }
        
        // Verificar se número do pedido é único (exceto se for o próprio)
        Optional<ControlePedidos> existingPedido = controlePedidosRepository.findByNumeroPedido(controlePedidos.getNumeroPedido());
        if (existingPedido.isPresent() && !existingPedido.get().getId().equals(controlePedidos.getId())) {
            throw new IllegalArgumentException("Já existe um pedido com este número");
        }
        
        // Verificar se fornecedor existe
        if (!fornecedorRepository.existsById(controlePedidos.getFornecedor().getId())) {
            throw new IllegalArgumentException("Fornecedor não encontrado");
        }
        
        // Verificar se ordem de compra existe
        if (!ordemCompraRepository.existsById(controlePedidos.getOrdemCompra().getId())) {
            throw new IllegalArgumentException("Ordem de compra não encontrada");
        }
        
        // Validar se data prevista de entrega não é no passado (para novos pedidos)
        if (controlePedidos.getId() == null && controlePedidos.getDataPrevistaEntrega().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data prevista de entrega não pode ser no passado");
        }
        
        // Validar se data real de entrega não é anterior à data do pedido
        if (controlePedidos.getDataRealEntrega() != null && controlePedidos.getDataPedido() != null &&
            controlePedidos.getDataRealEntrega().isBefore(controlePedidos.getDataPedido())) {
            throw new IllegalArgumentException("Data real de entrega não pode ser anterior à data do pedido");
        }
        
        // Validar valores de frete e desconto se informados
        if (controlePedidos.getValorFrete() != null && controlePedidos.getValorFrete().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do frete não pode ser negativo");
        }
        
        if (controlePedidos.getValorDesconto() != null && controlePedidos.getValorDesconto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do desconto não pode ser negativo");
        }
        
        // Validar chave NFe se informada (deve ter 44 caracteres)
        if (controlePedidos.getChaveNfe() != null && !controlePedidos.getChaveNfe().trim().isEmpty() && 
            controlePedidos.getChaveNfe().length() != 44) {
            throw new IllegalArgumentException("Chave da NFe deve ter exatamente 44 caracteres");
        }
    }
}
