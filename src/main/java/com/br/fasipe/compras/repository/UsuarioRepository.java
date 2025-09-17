package com.br.fasipe.compras.repository;

import com.br.fasipe.compras.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByLoginUsuario(String loginUsuario);
    
    @Query("SELECT u FROM Usuario u JOIN GrupoAprovador ga ON u.idUsuario = ga.usuario.idUsuario WHERE ga.idGrupoAprovador = :grupoAprovadorId AND ga.status = 'ativo'")
    Optional<Usuario> findByGrupoAprovadorId(@Param("grupoAprovadorId") Long grupoAprovadorId);
}