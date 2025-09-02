package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.model.Unimedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    List<Produto> findByDescricaoContainingIgnoreCase(String descricao);
    
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    
    List<Produto> findByUnidadeMedida(Unimedida unidadeMedida);
    
    List<Produto> findByIdAlmoxarifado(Integer idAlmoxarifado);
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueMinimo >= p.pontoPedido")
    List<Produto> findProdutosBaixoEstoque();
    
    @Query("SELECT p FROM Produto p WHERE p.temperaturaIdeal BETWEEN :tempMin AND :tempMax")
    List<Produto> findByTemperaturaIdealBetween(@Param("tempMin") BigDecimal tempMin, 
                                              @Param("tempMax") BigDecimal tempMax);
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueMaximo < :limite")
    List<Produto> findByEstoqueMaximoMenorQue(@Param("limite") Integer limite);
    
    @Query("SELECT p FROM Produto p WHERE p.estoqueMinimo > :limite")
    List<Produto> findByEstoqueMinimoMaiorQue(@Param("limite") Integer limite);
}
