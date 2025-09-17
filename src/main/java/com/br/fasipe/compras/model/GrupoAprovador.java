package com.br.fasipe.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GRUPOAPROVADOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoAprovador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPOAPROVADOR")
    private Long idGrupoAprovador;
    
    @ManyToOne
    @JoinColumn(name = "ID_USER", referencedColumnName = "IDUSUARIO")
    private Usuario usuario;
    
    @Column(name = "STATUS", length = 50)
    private String status;
}