package com.br.fasipe.compras.dto;

import java.time.LocalDate;
import java.util.List;

public class PedidoAgrupadoDTO {
    private String idPedido;
    private List<Long> idOrcamentos;
    private String nomeFornecedor;
    private Integer idFornecedor;
    private LocalDate dataEmissaoInicio;
    private LocalDate dataEmissaoFim;
    private String rangeDataEmissao;
    private Double valorTotal;
    private String status;
    private LocalDate dataGeracao;
    private String nomeUsuarioAprovador;
    private Integer quantidadeProdutos;
    private List<String> nomesProdutos;

    // Construtores
    public PedidoAgrupadoDTO() {}

    // Getters e Setters
    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public List<Long> getIdOrcamentos() {
        return idOrcamentos;
    }

    public void setIdOrcamentos(List<Long> idOrcamentos) {
        this.idOrcamentos = idOrcamentos;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public LocalDate getDataEmissaoInicio() {
        return dataEmissaoInicio;
    }

    public void setDataEmissaoInicio(LocalDate dataEmissaoInicio) {
        this.dataEmissaoInicio = dataEmissaoInicio;
    }

    public LocalDate getDataEmissaoFim() {
        return dataEmissaoFim;
    }

    public void setDataEmissaoFim(LocalDate dataEmissaoFim) {
        this.dataEmissaoFim = dataEmissaoFim;
    }

    public String getRangeDataEmissao() {
        return rangeDataEmissao;
    }

    public void setRangeDataEmissao(String rangeDataEmissao) {
        this.rangeDataEmissao = rangeDataEmissao;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getNomeUsuarioAprovador() {
        return nomeUsuarioAprovador;
    }

    public void setNomeUsuarioAprovador(String nomeUsuarioAprovador) {
        this.nomeUsuarioAprovador = nomeUsuarioAprovador;
    }

    public Integer getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public void setQuantidadeProdutos(Integer quantidadeProdutos) {
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public List<String> getNomesProdutos() {
        return nomesProdutos;
    }

    public void setNomesProdutos(List<String> nomesProdutos) {
        this.nomesProdutos = nomesProdutos;
    }
}