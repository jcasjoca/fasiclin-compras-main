package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "FORNECEDOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDFORNECEDOR")
    private Integer id;
    
    @Column(name = "ID_PESSOA", nullable = false, unique = true)
    private Integer idPessoa;
    
    @Column(name = "REPRESENT", length = 100)
    private String representante;
    
    @Column(name = "CONTREPRE", length = 15)
    private String contatoRepresentante;
    
    @Column(name = "DECRICAO", length = 250)
    private String descricao;
    
    @OneToMany(mappedBy = "fornecedor")
    private List<FornecedorProduto> produtos;
}
