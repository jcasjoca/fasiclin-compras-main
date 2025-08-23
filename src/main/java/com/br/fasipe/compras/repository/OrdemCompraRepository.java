package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.OrdemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompra, Integer> {
    
    List<OrdemCompra> findByStatus(OrdemCompra.StatusOrdemCompra status);
    
    List<OrdemCompra> findByDataOrdemBetween(LocalDate inicio, LocalDate fim);
    
    List<OrdemCompra> findByDataPrevistaLessThanEqual(LocalDate data);
}
