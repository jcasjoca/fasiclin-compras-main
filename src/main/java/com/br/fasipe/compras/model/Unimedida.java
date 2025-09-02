package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UNIMEDIDA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unimedida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUNMEDI")
    private Integer id;
    
    @Column(name = "DESCRICAO", length = 50, nullable = false)
    private String descricao;
    
    @Column(name = "UNIABREV", length = 3, nullable = false, unique = true)
    private String unidadeAbreviacao;
}
