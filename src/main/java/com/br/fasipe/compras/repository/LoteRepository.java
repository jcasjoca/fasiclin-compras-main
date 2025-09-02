package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.OrdemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    
    List<Lote> findByOrdemCompra(OrdemCompra ordemCompra);
    
    List<Lote> findByDataVencimentoBefore(LocalDate dataVencimento);
    
    List<Lote> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    @Query("SELECT l FROM Lote l WHERE l.ordemCompra.id = :ordemCompraId")
    List<Lote> findByOrdemCompraId(@Param("ordemCompraId") Integer ordemCompraId);
    
    @Query("SELECT SUM(l.quantidade) FROM Lote l WHERE l.ordemCompra.id = :ordemCompraId")
    Integer getTotalQuantidadeByOrdemCompra(@Param("ordemCompraId") Integer ordemCompraId);
    
    @Query("SELECT l FROM Lote l WHERE l.dataVencimento <= :dataLimite ORDER BY l.dataVencimento ASC")
    List<Lote> findLotesVencendoAte(@Param("dataLimite") LocalDate dataLimite);
}
