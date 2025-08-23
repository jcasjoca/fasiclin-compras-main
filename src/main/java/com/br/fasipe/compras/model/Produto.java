package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    
    @Column(name = "NOME", nullable = false, length = 50)
    private String nome;
    
    @Column(name = "DESCRICAO", nullable = false, length = 250)
    private String descricao;
    
    @ManyToOne
    @JoinColumn(name = "ID_ALMOX")
    private Almoxarifado almoxarifado;
    
    @ManyToOne
    @JoinColumn(name = "ID_UNMEDI", nullable = false)
    private UnidadeMedida unidadeMedida;
    
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
    
    @OneToMany(mappedBy = "produto")
    private List<FornecedorProduto> fornecedores;
    
    @OneToMany(mappedBy = "produto")
    private List<Estoque> estoques;
    
    @OneToMany(mappedBy = "produto")
    private List<ItemOrdemCompra> itensOrdemCompra;
}
