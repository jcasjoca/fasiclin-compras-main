package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.GrupoAprovador;
import com.br.fasipe.compras.repository.GrupoAprovadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grupoaprovador")
public class GrupoAprovadorController {

    @Autowired
    private GrupoAprovadorRepository grupoAprovadorRepository;

    @GetMapping
    public List<GrupoAprovador> listarTodos() {
        return grupoAprovadorRepository.findAll();
    }
}