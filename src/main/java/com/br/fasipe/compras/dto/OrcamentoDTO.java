package com.br.fasipe.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoDTO {
    
    private Long idOrcamento;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private LocalDate dataEntrega;
    
    // Dados do fornecedor
    private Integer idFornecedor;
    private String nomeFornecedor;
    private String representante;
    private String contatoRepresentante;
    private String descricaoFornecedor;
    
    // Dados do produto
    private Integer idProduto;
    private String nomeProduto;
    private String descricaoProduto;
    private String codigoBarras;
    
    // Dados da unidade de medida
    private Integer idUnidadeMedida;
    private String descricaoUnidadeMedida;
    private String unidadeAbreviacao;
    
    private String garantia;
    private String condicoesPagamento;
    private BigDecimal precoCompra;
    private Integer quantidade;
    private Long idGrupoAprovador;
    private Long idUserApprove;
    private String nomeUsuarioAprovador;
    private String status;
    private LocalDate dataGeracao;
    private LocalDateTime dataHoraAprovacao;
    private String observacoes; // Adicionado para a tela de aprovação

    // Este método calcula o valor total e será incluído no JSON de resposta.
    public BigDecimal getValorTotal() {
        if (precoCompra != null && quantidade != null && quantidade > 0) {
            return precoCompra.multiply(BigDecimal.valueOf(quantidade));
        }
        return BigDecimal.ZERO;
    }
}
