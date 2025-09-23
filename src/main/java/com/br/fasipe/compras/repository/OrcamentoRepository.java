package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    @Query("SELECT o FROM Orcamento o WHERE o.status = 'Pendente'")
    List<Orcamento> findByStatusPendente();
    
    @Transactional
    @Modifying
    @Query("UPDATE Orcamento o SET o.status = 'reprovado' WHERE o.produto.id IN :produtoIds AND o.idOrcamento NOT IN :orcamentoIdsAprovados AND o.status = 'Pendente'")
    void reprovarConcorrentes(@Param("produtoIds") Set<Integer> produtoIds, @Param("orcamentoIdsAprovados") List<Long> orcamentoIdsAprovados);

    // CORREÇÃO: Adicionados filtros para valorMinimo e valorMaximo
    @Query("SELECT o from Orcamento o WHERE " +
           "(:dataInicial IS NULL OR " +
           "  (LOWER(o.status) = 'aprovado' AND o.dataGeracao >= :dataInicial) OR " +
           "  (LOWER(o.status) <> 'aprovado' AND o.dataEmissao >= :dataInicial)" +
           ") AND " +
           "(:dataFinal IS NULL OR " +
           "  (LOWER(o.status) = 'aprovado' AND o.dataGeracao <= :dataFinal) OR " +
           "  (LOWER(o.status) <> 'aprovado' AND o.dataEmissao <= :dataFinal)" +
           ") AND " +
           "(:fornecedorNome IS NULL OR LOWER(o.fornecedor.descricao) LIKE LOWER(CONCAT('%', :fornecedorNome, '%'))) AND " +
           "(:produtoNome IS NULL OR LOWER(o.produto.nome) LIKE LOWER(CONCAT('%', :produtoNome, '%'))) AND " +
           "(:idOrcamento IS NULL OR o.idOrcamento = :idOrcamento) AND " +
           "(:status IS NULL OR LOWER(o.status) = LOWER(:status)) AND " +
           "(:valorMinimo IS NULL OR (o.precoCompra * o.quantidade) >= :valorMinimo) AND " +
           "(:valorMaximo IS NULL OR (o.precoCompra * o.quantidade) <= :valorMaximo)")
    List<Orcamento> findWithFilters(
            @Param("dataInicial") LocalDate dataInicial, 
            @Param("dataFinal") LocalDate dataFinal, 
            @Param("fornecedorNome") String fornecedorNome, 
            @Param("produtoNome") String produtoNome, 
            @Param("idOrcamento") Long idOrcamento,
            @Param("status") String status,
            @Param("valorMinimo") BigDecimal valorMinimo,
            @Param("valorMaximo") BigDecimal valorMaximo
    );
}
