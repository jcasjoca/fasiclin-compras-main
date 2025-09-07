package com.br.fasipe.compras.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecprod")
public class FornecProd {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codfornecprod;
    
    @Column(name = "codfornecedor")
    private Integer codFornecedor;
    
    @Column(name = "codproduto")
    private Integer codProduto;
    
    @Column(name = "valorunid")
    private Double valorUnid;
    
    // Getters and Setters
    public Integer getCodfornecprod() {
        return codfornecprod;
    }
    
    public void setCodfornecprod(Integer codfornecprod) {
        this.codfornecprod = codfornecprod;
    }
    
    public Integer getCodFornecedor() {
        return codFornecedor;
    }
    
    public void setCodFornecedor(Integer codFornecedor) {
        this.codFornecedor = codFornecedor;
    }
    
    public Integer getCodProduto() {
        return codProduto;
    }
    
    public void setCodProduto(Integer codProduto) {
        this.codProduto = codProduto;
    }
    
    public Double getValorUnid() {
        return valorUnid;
    }
    
    public void setValorUnid(Double valorUnid) {
        this.valorUnid = valorUnid;
    }
}
