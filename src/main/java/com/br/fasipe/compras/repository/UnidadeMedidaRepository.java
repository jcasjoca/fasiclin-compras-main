package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Integer> {
    
    UnidadeMedida findByAbreviacao(String abreviacao);
    
    List<UnidadeMedida> findByDescricaoContainingIgnoreCase(String descricao);
}
