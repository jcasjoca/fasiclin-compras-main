package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ITEM_ORDCOMP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrdemCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDITEMORD")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_ORDCOMP", nullable = false)
    private OrdemCompra ordemCompra;
    
    @ManyToOne
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;
    
    @Column(name = "QNTD", nullable = false)
    private Integer quantidade;
    
    @Column(name = "VALOR", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "DATAVENC", nullable = false)
    private LocalDate dataVencimento;
}
