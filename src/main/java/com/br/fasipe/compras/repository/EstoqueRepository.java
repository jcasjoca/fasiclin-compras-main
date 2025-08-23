package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {
    
    List<Estoque> findByProduto(Produto produto);
    
    List<Estoque> findByLote(Lote lote);
    
    @Query("SELECT SUM(e.quantidadeEstoque) FROM Estoque e WHERE e.produto = :produto")
    Integer findQuantidadeTotalByProduto(Produto produto);
}
