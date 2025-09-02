package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.OrdemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompra, Integer> {
    
    List<OrdemCompra> findByStatus(OrdemCompra.StatusOrdemCompra status);
    
    List<OrdemCompra> findByDataOrdemBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<OrdemCompra> findByDataPrevistaBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<OrdemCompra> findByDataEntregaBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<OrdemCompra> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);
    
    @Query("SELECT o FROM OrdemCompra o WHERE o.dataPrevista < CURRENT_DATE AND o.status != 'CONC'")
    List<OrdemCompra> findOrdensAtrasadas();
    
    @Query("SELECT o FROM OrdemCompra o WHERE o.dataEntrega IS NULL AND o.status = 'ANDA'")
    List<OrdemCompra> findOrdensEmAndamentoSemEntrega();
    
    @Query("SELECT AVG(o.valor) FROM OrdemCompra o WHERE o.status = 'CONC'")
    BigDecimal getValorMedioOrdensConcluidas();
    
    @Query("SELECT COUNT(o) FROM OrdemCompra o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrdemCompra.StatusOrdemCompra status);
}
