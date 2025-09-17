package com.br.fasipe.compras.model;

import jakarta.persistence.*;

@Entity
@Table(name = "FORNECPROD")
public class FornecProd {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDFORNECPROD")
    private Integer id;
    
    @Column(name = "ID_FORNECEDOR")
    private Integer idFornecedor;
    
    @Column(name = "ID_PRODUTO")
    private Integer idProduto;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getIdFornecedor() {
        return idFornecedor;
    }
    
    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }
    
    public Integer getIdProduto() {
        return idProduto;
    }
    
    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }
}
