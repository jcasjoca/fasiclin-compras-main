package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "UNIMEDIDA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeMedida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUNMEDI")
    private Integer id;
    
    @Column(name = "DESCRICAO", nullable = false, length = 50)
    private String descricao;
    
    @Column(name = "UNIABREV", nullable = false, length = 3, unique = true)
    private String abreviacao;
    
    @OneToMany(mappedBy = "unidadeMedida")
    private List<Produto> produtos;
}
