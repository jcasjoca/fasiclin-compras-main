package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'Pendente'")
    List<Orcamento> findByStatusPendente();
    
    @Transactional
    @Modifying
    // CORREÇÃO APLICADA AQUI: o.produto.id
    @Query("UPDATE Orcamento o SET o.status = 'reprovado' WHERE o.produto.id IN :produtoIds AND o.idOrcamento NOT IN :orcamentoIdsAprovados AND o.status = 'Pendente'")
    void reprovarConcorrentes(@Param("produtoIds") Set<Long> produtoIds, @Param("orcamentoIdsAprovados") List<Long> orcamentoIdsAprovados);
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetween(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal);
    
    // CORREÇÃO APLICADA AQUI: o.fornecedor.id
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.fornecedor.id = :fornecedorId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndFornecedor(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("fornecedorId") Long fornecedorId);
    
    // CORREÇÃO APLICADA AQUI: o.produto.id
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.produto.id = :produtoId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndProduto(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("produtoId") Long produtoId);
    
    // CORREÇÃO APLICADA AQUI: o.fornecedor.id e o.produto.id
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'aprovado' AND o.dataGeracao BETWEEN :dataInicial AND :dataFinal AND o.fornecedor.id = :fornecedorId AND o.produto.id = :produtoId")
    List<Orcamento> findByStatusAprovadoAndDataGeracaoBetweenAndFornecedorAndProduto(@Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal, @Param("fornecedorId") Long fornecedorId, @Param("produtoId") Long produtoId);

    // CORREÇÃO APLICADA AQUI: o.fornecedor.id e o.produto.id
    @Query("SELECT o from Orcamento o WHERE " +
           "(:dataInicial IS NULL OR o.dataGeracao >= :dataInicial) AND " +
           "(:dataFinal IS NULL OR o.dataGeracao <= :dataFinal) AND " +
           "(:fornecedorId IS NULL OR o.fornecedor.id = :fornecedorId) AND " +
           "(:produtoId IS NULL OR o.produto.id = :produtoId) AND " +
           "(:idOrcamento IS NULL OR o.idOrcamento = :idOrcamento) AND " +
           "o.status = 'aprovado'")
    List<Orcamento> findWithFilters(
            @Param("dataInicial") LocalDate dataInicial, 
            @Param("dataFinal") LocalDate dataFinal, 
            @Param("fornecedorId") Long fornecedorId, 
            @Param("produtoId") Long produtoId, 
            @Param("idOrcamento") Long idOrcamento
    );
}