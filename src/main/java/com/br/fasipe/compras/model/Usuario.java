package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUSUARIO")
    private Long idUsuario;
    
    @Column(name = "ID_PROFISSIO")
    private Long idProfissao;
    
    @Column(name = "ID_PESSOAFIS")
    private Long idPessoaFisica;
    
    @Column(name = "LOGUSUARIO", length = 50)
    private String loginUsuario;
    
    @Column(name = "SENHAUSUA", length = 100)
    private String senhaUsuario;
}