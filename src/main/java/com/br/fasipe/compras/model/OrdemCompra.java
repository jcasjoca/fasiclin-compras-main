package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ORDEMCOMPRA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDORDCOMP")
    private Integer id;
    
    @Column(name = "STATUSORD", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusOrdemCompra status;
    
    @Column(name = "VALOR", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "DATAPREV", nullable = false)
    private LocalDate dataPrevista;
    
    @Column(name = "DATAORDEM", nullable = false)
    private LocalDate dataOrdem;
    
    @Column(name = "DATAENTRE", nullable = false)
    private LocalDate dataEntrega;
    
    @OneToMany(mappedBy = "ordemCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrdemCompra> itens;
    
    @OneToMany(mappedBy = "ordemCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lote> lotes;
    
    public enum StatusOrdemCompra {
        PEND, ANDA, CONC
    }
}
