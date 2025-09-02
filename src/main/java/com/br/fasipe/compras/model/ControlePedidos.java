package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CONTROLEPEDIDOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlePedidos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCONTROLEPEDIDO")
    private Integer id;
    
    @Column(name = "NUMERO_PEDIDO", nullable = false, unique = true)
    private String numeroPedido;
    
    @ManyToOne
    @JoinColumn(name = "ID_FORNECEDOR", nullable = false)
    private Fornecedor fornecedor;
    
    @ManyToOne
    @JoinColumn(name = "ID_ORDCOMP", nullable = false)
    private OrdemCompra ordemCompra;
    
    @Column(name = "DATA_PEDIDO", nullable = false)
    private LocalDate dataPedido;
    
    @Column(name = "DATA_PREVISTA_ENTREGA", nullable = false)
    private LocalDate dataPrevistaEntrega;
    
    @Column(name = "DATA_REAL_ENTREGA")
    private LocalDate dataRealEntrega;
    
    @Column(name = "VALOR_TOTAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(name = "VALOR_FRETE", precision = 10, scale = 2)
    private BigDecimal valorFrete;
    
    @Column(name = "VALOR_DESCONTO", precision = 10, scale = 2)
    private BigDecimal valorDesconto;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_PEDIDO", nullable = false)
    private StatusPedido statusPedido;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PAGAMENTO", nullable = false)
    private TipoPagamento tipoPagamento;
    
    @Column(name = "OBSERVACOES", length = 500)
    private String observacoes;
    
    @Column(name = "NUMERO_NOTA_FISCAL", length = 50)
    private String numeroNotaFiscal;
    
    @Column(name = "CHAVE_NFE", length = 44)
    private String chaveNfe;
    
    @Column(name = "ID_USUARIO_RESPONSAVEL", nullable = false)
    private Integer idUsuarioResponsavel;
    
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDate dataCriacao;
    
    @Column(name = "DATA_ULTIMA_ATUALIZACAO")
    private LocalDate dataUltimaAtualizacao;
    
    public enum StatusPedido {
        SOLICITADO,     // Pedido solicitado
        CONFIRMADO,     // Confirmado pelo fornecedor
        EM_TRANSITO,    // Em trânsito
        ENTREGUE,       // Entregue
        CANCELADO,      // Cancelado
        DEVOLVIDO       // Devolvido
    }
    
    public enum TipoPagamento {
        A_VISTA,        // À vista
        BOLETO,         // Boleto bancário
        CARTAO,         // Cartão de crédito/débito
        PIX,            // PIX
        TRANSFERENCIA,  // Transferência bancária
        PRAZO_30,       // Prazo 30 dias
        PRAZO_60,       // Prazo 60 dias
        PRAZO_90        // Prazo 90 dias
    }
}
