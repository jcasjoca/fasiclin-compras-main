package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "LOTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLOTE")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_ORDCOMP", nullable = false)
    private OrdemCompra ordemCompra;
    
    @Column(name = "DATAVENC", nullable = false)
    private LocalDate dataVencimento;
    
    @Column(name = "QNTD", nullable = false)
    private Integer quantidade;
}
