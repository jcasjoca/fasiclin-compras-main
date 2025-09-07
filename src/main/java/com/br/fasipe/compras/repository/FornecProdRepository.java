package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.FornecProd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecProdRepository extends JpaRepository<FornecProd, Integer> {
}
