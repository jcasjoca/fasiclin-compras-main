package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "MOVIMENTACAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMOVIMENTACAO")
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "ID_ESTOQUE", nullable = false)
    private Estoque estoque;
    
    @Column(name = "ID_USUARIO", nullable = false)
    private Integer idUsuario;
    
    @Column(name = "ID_SETOR_ORIGEM", nullable = false)
    private Integer idSetorOrigem;
    
    @Column(name = "ID_SETOR_DESTINO", nullable = false)
    private Integer idSetorDestino;
    
    @Column(name = "QTDMOVIM", nullable = false)
    private Integer quantidadeMovimentada;
    
    @Column(name = "DATAMOVIM", nullable = false)
    private LocalDate dataMovimentacao;
    
    @Column(name = "TIPOMOVIM", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipoMovimentacao;
    
    public enum TipoMovimentacao {
        ENTRADA, SAIDA
    }
}
