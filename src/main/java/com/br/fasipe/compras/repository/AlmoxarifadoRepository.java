package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Almoxarifado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlmoxarifadoRepository extends JpaRepository<Almoxarifado, Integer> {
    
    Almoxarifado findByIdSetor(Integer idSetor);
    
    List<Almoxarifado> findByNomeContainingIgnoreCase(String nome);
}
