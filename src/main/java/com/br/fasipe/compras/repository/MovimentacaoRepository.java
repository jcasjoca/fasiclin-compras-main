package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.model.Movimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    
    List<Movimentacao> findByEstoque(Estoque estoque);
    
    List<Movimentacao> findByTipoMovimentacao(TipoMovimentacao tipoMovimentacao);
    
    List<Movimentacao> findByDataMovimentacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<Movimentacao> findByIdSetorOrigem(Integer idSetorOrigem);
    
    List<Movimentacao> findByIdSetorDestino(Integer idSetorDestino);
}
