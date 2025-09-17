package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.GrupoAprovador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoAprovadorRepository extends JpaRepository<GrupoAprovador, Long> {
    
    @Query("SELECT ga FROM GrupoAprovador ga WHERE ga.usuario.idUsuario = :usuarioId AND ga.status = 'ativo'")
    List<GrupoAprovador> findByUsuarioIdAndStatusAtivo(@Param("usuarioId") Long usuarioId);
}