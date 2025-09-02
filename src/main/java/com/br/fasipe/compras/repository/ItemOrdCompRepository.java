package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Item_OrdComp;
import com.br.fasipe.compras.model.OrdemCompra;
import com.br.fasipe.compras.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemOrdCompRepository extends JpaRepository<Item_OrdComp, Integer> {
    
    List<Item_OrdComp> findByOrdemCompra(OrdemCompra ordemCompra);
    
    List<Item_OrdComp> findByProduto(Produto produto);
    
    List<Item_OrdComp> findByDataVencimentoBefore(LocalDate dataVencimento);
    
    @Query("SELECT i FROM Item_OrdComp i WHERE i.ordemCompra.id = :ordemCompraId")
    List<Item_OrdComp> findByOrdemCompraId(@Param("ordemCompraId") Integer ordemCompraId);
    
    @Query("SELECT i FROM Item_OrdComp i WHERE i.produto.id = :produtoId")
    List<Item_OrdComp> findByProdutoId(@Param("produtoId") Integer produtoId);
    
    @Query("SELECT SUM(i.quantidade) FROM Item_OrdComp i WHERE i.ordemCompra.id = :ordemCompraId")
    Integer getTotalQuantidadeByOrdemCompra(@Param("ordemCompraId") Integer ordemCompraId);
}
