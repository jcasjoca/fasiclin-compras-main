package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Almoxarifado;
import com.br.fasipe.compras.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    List<Produto> findByAlmoxarifado(Almoxarifado almoxarifado);
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueMinimo >= (SELECT SUM(e.quantidadeEstoque) FROM Estoque e WHERE e.produto = p)")
    List<Produto> findProdutosComEstoqueBaixo();
}
