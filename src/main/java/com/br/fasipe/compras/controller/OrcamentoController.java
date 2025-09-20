package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.Orcamento;
import com.br.fasipe.compras.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orcamento")
public class OrcamentoController {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @GetMapping
    public List<Orcamento> listarTodos() {
        return orcamentoRepository.findAll();
    }
}