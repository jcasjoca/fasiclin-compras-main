package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ORCAMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORCAMENTO")
    private Long idOrcamento;
    
    @Column(name = "DATA_EMISSAO")
    private LocalDate dataEmissao;
    
    @Column(name = "DATA_VALIDADE")
    private LocalDate dataValidade;
    
    @Column(name = "DATA_ENTREGA")
    private LocalDate dataEntrega;
    
    @ManyToOne
    @JoinColumn(name = "ID_FORNECEDOR", referencedColumnName = "IDFORNECEDOR")
    private Fornecedor fornecedor;
    
    @ManyToOne
    @JoinColumn(name = "ID_PRODUTO", referencedColumnName = "IDPRODUTO")
    private Produto produto;
    
    @ManyToOne
    @JoinColumn(name = "ID_UNMEDI", referencedColumnName = "IDUNMEDI")
    private Unimedida unidadeMedida;
    
    @Column(name = "GARANTIA", length = 250)
    private String garantia;
    
    @Column(name = "CONDICOES_PAGAMENTO", length = 250)
    private String condicoesPagamento;
    
    @Column(name = "PRECO_COMPRA", precision = 10, scale = 2)
    private BigDecimal precoCompra;
    
    @Column(name = "QUANTIDADE")
    private Integer quantidade;
    
    @ManyToOne
    @JoinColumn(name = "ID_GRUPOAPROVADOR", referencedColumnName = "ID_GRUPOAPROVADOR")
    private GrupoAprovador grupoAprovador;
    
    @ManyToOne
    @JoinColumn(name = "ID_USERAPROVE", referencedColumnName = "IDUSUARIO")
    private Usuario usuarioAprovador;
    
    @Column(name = "STATUS", length = 50)
    private String status;
    
    @Column(name = "DATA_GERACAO")
    private LocalDate dataGeracao;
}