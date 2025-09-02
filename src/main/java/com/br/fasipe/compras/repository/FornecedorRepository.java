package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
    
    Optional<Fornecedor> findByIdPessoa(Integer idPessoa);
    
    @Query("SELECT f FROM Fornecedor f WHERE f.representante LIKE %:representante%")
    java.util.List<Fornecedor> findByRepresentanteContaining(@Param("representante") String representante);
    
    @Query("SELECT f FROM Fornecedor f WHERE f.descricao LIKE %:descricao%")
    java.util.List<Fornecedor> findByDescricaoContaining(@Param("descricao") String descricao);
}
