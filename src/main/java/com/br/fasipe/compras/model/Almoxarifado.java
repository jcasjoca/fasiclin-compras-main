package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ALMOXARIFADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Almoxarifado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDALMOX")
    private Integer id;
    
    @Column(name = "ID_SETOR", nullable = false)
    private Integer idSetor;
    
    @Column(name = "NOMEALMO", nullable = false, length = 100, unique = true)
    private String nome;
    
    @OneToMany(mappedBy = "almoxarifado")
    private List<Produto> produtos;
}
