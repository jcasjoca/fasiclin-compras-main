package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'pendente de aprovacao'")
    List<Orcamento> findByStatusPendente();
    
    @Modifying
    @Query("UPDATE Orcamento o SET o.status = 'reprovado' WHERE o.produto.id IN :produtoIds AND o.idOrcamento NOT IN :orcamentoIdsAprovados AND o.status = 'pendente de aprovacao'")
    void reprovarConcorrentes(@Param("produtoIds") Set<Integer> produtoIds, @Param("orcamentoIdsAprovados") List<Long> orcamentoIdsAprovados);
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetween(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal);
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.fornecedor.id = :fornecedorId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndFornecedor(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("fornecedorId") Integer fornecedorId);
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.produto.id = :produtoId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndProduto(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("produtoId") Integer produtoId);
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.fornecedor.id = :fornecedorId AND o.produto.id = :produtoId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndFornecedorAndProduto(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("fornecedorId") Integer fornecedorId, @Param("produtoId") Integer produtoId);
}