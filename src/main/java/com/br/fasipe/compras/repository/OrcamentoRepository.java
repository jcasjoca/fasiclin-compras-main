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
    @Query("UPDATE Orcamento o SET o.status = 'reprovado' WHERE o.produto.id IN :produtoIds AND o.idOrcamento NOT IN :orcamentoIdsAprovados AND o.status = 'Pendente'")
    void reprovarConcorrentes(@Param("produtoIds") Set<Integer> produtoIds, @Param("orcamentoIdsAprovados") List<Long> orcamentoIdsAprovados);

    /**
     * NOVA QUERY DE FILTRO - A MAIS IMPORTANTE
     * Esta query agora lida corretamente com todos os filtros opcionais.
     */
    // Dentro da interface OrcamentoRepository

@Query("SELECT o from Orcamento o WHERE " +
       "(:dataInicial IS NULL OR " +
       "  (o.status = 'aprovado' AND o.dataGeracao >= :dataInicial) OR " +
       "  (o.status <> 'aprovado' AND o.dataEmissao >= :dataInicial)" +
       ") AND " +
       "(:dataFinal IS NULL OR " +
       "  (o.status = 'aprovado' AND o.dataGeracao <= :dataFinal) OR " +
       "  (o.status <> 'aprovado' AND o.dataEmissao <= :dataFinal)" +
       ") AND " +
       // MELHORIA: Usando LOWER para busca case-insensitive
       "(:fornecedorNome IS NULL OR LOWER(o.fornecedor.descricao) LIKE LOWER(CONCAT('%', :fornecedorNome, '%'))) AND " +
       // MELHORIA: Usando LOWER para busca case-insensitive
       "(:produtoNome IS NULL OR LOWER(o.produto.nome) LIKE LOWER(CONCAT('%', :produtoNome, '%'))) AND " +
       "(:idOrcamento IS NULL OR o.idOrcamento = :idOrcamento) AND " +
       // MELHORIA: Usando LOWER para garantir a comparação correta do status
       "(:status IS NULL OR LOWER(o.status) = LOWER(:status))")
List<Orcamento> findWithFilters(
        @Param("dataInicial") LocalDate dataInicial, 
        @Param("dataFinal") LocalDate dataFinal, 
        @Param("fornecedorNome") String fornecedorNome, 
        @Param("produtoNome") String produtoNome, 
        @Param("idOrcamento") Long idOrcamento,
        @Param("status") String status
);
}

