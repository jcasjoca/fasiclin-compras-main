package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    
    List<Movimentacao> findByIdEstoque(Integer idEstoque);
    
    List<Movimentacao> findByIdUsuario(Integer idUsuario);
    
    List<Movimentacao> findByTipoMovimentacao(Movimentacao.TipoMovimentacao tipoMovimentacao);
    
    List<Movimentacao> findByDataMovimentacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    List<Movimentacao> findByIdSetorOrigem(Integer idSetorOrigem);
    
    List<Movimentacao> findByIdSetorDestino(Integer idSetorDestino);
    
    @Query("SELECT m FROM Movimentacao m WHERE m.idEstoque = :idEstoque AND m.tipoMovimentacao = :tipoMovimentacao")
    List<Movimentacao> findByEstoqueAndTipo(@Param("idEstoque") Integer idEstoque, 
                                          @Param("tipoMovimentacao") Movimentacao.TipoMovimentacao tipoMovimentacao);
    
    @Query("SELECT SUM(m.quantidadeMovimentacao) FROM Movimentacao m WHERE m.idEstoque = :idEstoque AND m.tipoMovimentacao = 'ENTRADA'")
    Integer getTotalEntradasByEstoque(@Param("idEstoque") Integer idEstoque);
    
    @Query("SELECT SUM(m.quantidadeMovimentacao) FROM Movimentacao m WHERE m.idEstoque = :idEstoque AND m.tipoMovimentacao = 'SAIDA'")
    Integer getTotalSaidasByEstoque(@Param("idEstoque") Integer idEstoque);
}
