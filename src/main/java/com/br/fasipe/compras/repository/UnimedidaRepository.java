package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Unimedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnimedidaRepository extends JpaRepository<Unimedida, Integer> {
    
    Optional<Unimedida> findByUnidadeAbreviacao(String unidadeAbreviacao);
    
    List<Unimedida> findByDescricaoContainingIgnoreCase(String descricao);
    
    @Query("SELECT u FROM Unimedida u WHERE u.descricao LIKE %:descricao%")
    List<Unimedida> findByDescricaoContaining(@Param("descricao") String descricao);
    
    @Query("SELECT u FROM Unimedida u ORDER BY u.descricao ASC")
    List<Unimedida> findAllOrderByDescricao();
}
