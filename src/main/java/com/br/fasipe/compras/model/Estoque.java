package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ESTOQUE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDESTOQUE")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;
    
    @ManyToOne
    @JoinColumn(name = "ID_LOTE", nullable = false)
    private Lote lote;
    
    @Column(name = "QTDESTOQUE", nullable = false)
    private Integer quantidadeEstoque;
    
    @OneToMany(mappedBy = "estoque")
    private List<Movimentacao> movimentacoes;
}
