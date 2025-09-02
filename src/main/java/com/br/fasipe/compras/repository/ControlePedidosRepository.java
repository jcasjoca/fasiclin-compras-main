package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.ControlePedidos;
import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.OrdemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlePedidosRepository extends JpaRepository<ControlePedidos, Integer> {
    
    Optional<ControlePedidos> findByNumeroPedido(String numeroPedido);
    
    List<ControlePedidos> findByFornecedor(Fornecedor fornecedor);
    
    List<ControlePedidos> findByOrdemCompra(OrdemCompra ordemCompra);
    
    List<ControlePedidos> findByStatusPedido(ControlePedidos.StatusPedido statusPedido);
    
    List<ControlePedidos> findByTipoPagamento(ControlePedidos.TipoPagamento tipoPagamento);
    
    List<ControlePedidos> findByDataPedidoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<ControlePedidos> findByDataPrevistaEntregaBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<ControlePedidos> findByDataRealEntregaBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<ControlePedidos> findByValorTotalBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);
    
    Optional<ControlePedidos> findByNumeroNotaFiscal(String numeroNotaFiscal);
    
    Optional<ControlePedidos> findByChaveNfe(String chaveNfe);
    
    List<ControlePedidos> findByIdUsuarioResponsavel(Integer idUsuarioResponsavel);
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.fornecedor.id = :fornecedorId")
    List<ControlePedidos> findByFornecedorId(@Param("fornecedorId") Integer fornecedorId);
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.ordemCompra.id = :ordemCompraId")
    List<ControlePedidos> findByOrdemCompraId(@Param("ordemCompraId") Integer ordemCompraId);
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.dataPrevistaEntrega < CURRENT_DATE AND cp.statusPedido NOT IN ('ENTREGUE', 'CANCELADO', 'DEVOLVIDO')")
    List<ControlePedidos> findPedidosAtrasados();
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.dataPrevistaEntrega = CURRENT_DATE AND cp.statusPedido NOT IN ('ENTREGUE', 'CANCELADO', 'DEVOLVIDO')")
    List<ControlePedidos> findPedidosVencendoHoje();
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.dataPrevistaEntrega BETWEEN CURRENT_DATE AND :dataLimite AND cp.statusPedido NOT IN ('ENTREGUE', 'CANCELADO', 'DEVOLVIDO')")
    List<ControlePedidos> findPedidosVencendoAte(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.numeroNotaFiscal IS NULL AND cp.statusPedido = 'ENTREGUE'")
    List<ControlePedidos> findPedidosEntreguesSemNotaFiscal();
    
    @Query("SELECT SUM(cp.valorTotal) FROM ControlePedidos cp WHERE cp.statusPedido = :statusPedido")
    BigDecimal getSomaValorTotalByStatus(@Param("statusPedido") ControlePedidos.StatusPedido statusPedido);
    
    @Query("SELECT COUNT(cp) FROM ControlePedidos cp WHERE cp.statusPedido = :statusPedido")
    Long countByStatusPedido(@Param("statusPedido") ControlePedidos.StatusPedido statusPedido);
    
    @Query("SELECT AVG(cp.valorTotal) FROM ControlePedidos cp WHERE cp.statusPedido = 'ENTREGUE'")
    BigDecimal getValorMedioPedidosEntregues();
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.valorTotal > :valorMinimo ORDER BY cp.valorTotal DESC")
    List<ControlePedidos> findPedidosComValorMaiorQue(@Param("valorMinimo") BigDecimal valorMinimo);
    
    @Query("SELECT cp FROM ControlePedidos cp WHERE cp.dataCriacao BETWEEN :dataInicio AND :dataFim")
    List<ControlePedidos> findByCriacaoBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}
