package com.br.fasipe.compras.controller;

import com.br.fasipe.compras.model.FornecProd;
import com.br.fasipe.compras.repository.FornecProdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fornecprod")
public class FornecProdController {
    
    @Autowired
    private FornecProdRepository fornecProdRepository;
    
    @GetMapping
    public List<FornecProd> getAllFornecProd() {
        return fornecProdRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Optional<FornecProd> getFornecProdById(@PathVariable Integer id) {
        return fornecProdRepository.findById(id);
    }
}
