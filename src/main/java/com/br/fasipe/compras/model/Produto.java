package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPRODUTO")
    private Integer id;
    
    @Column(name = "NOME", length = 50, nullable = false)
    private String nome;
    
    @Column(name = "DESCRICAO", length = 250, nullable = false)
    private String descricao;
    
    @Column(name = "ID_ALMOX")
    private Integer idAlmoxarifado;
    
    @ManyToOne
    @JoinColumn(name = "ID_UNMEDI", referencedColumnName = "IDUNMEDI", nullable = false)
    private Unimedida unidadeMedida;
    
    @Column(name = "CODBARRAS", length = 250)
    private String codigoBarras;
    
    @Column(name = "TEMPIDEAL", precision = 3, scale = 1)
    private BigDecimal temperaturaIdeal;
    
    @Column(name = "STQMAX", nullable = false)
    private Integer estoqueMaximo;
    
    @Column(name = "STQMIN", nullable = false)
    private Integer estoqueMinimo;
    
    @Column(name = "PNTPEDIDO", nullable = false)
    private Integer pontoPedido;
}
