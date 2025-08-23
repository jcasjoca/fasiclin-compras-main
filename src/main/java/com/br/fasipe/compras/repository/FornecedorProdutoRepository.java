package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.FornecedorProduto;
import com.br.fasipe.compras.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FornecedorProdutoRepository extends JpaRepository<FornecedorProduto, Integer> {
    
    List<FornecedorProduto> findByFornecedor(Fornecedor fornecedor);
    
    List<FornecedorProduto> findByProduto(Produto produto);
}
