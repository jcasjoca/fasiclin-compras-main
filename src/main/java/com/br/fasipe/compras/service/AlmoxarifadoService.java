package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Almoxarifado;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.AlmoxarifadoRepository;
import com.br.fasipe.compras.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlmoxarifadoService {

    @Autowired
    private AlmoxarifadoRepository almoxarifadoRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<Almoxarifado> listarTodos() {
        return almoxarifadoRepository.findAll();
    }
    
    public Optional<Almoxarifado> buscarPorId(Integer id) {
        return almoxarifadoRepository.findById(id);
    }
    
    public Almoxarifado buscarPorIdSetor(Integer idSetor) {
        return almoxarifadoRepository.findByIdSetor(idSetor);
    }
    
    public List<Almoxarifado> buscarPorNome(String nome) {
        return almoxarifadoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    @Transactional
    public Almoxarifado salvar(Almoxarifado almoxarifado) {
        // Validações de negócio podem ser adicionadas aqui
        return almoxarifadoRepository.save(almoxarifado);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<Almoxarifado> almoxarifadoOpt = almoxarifadoRepository.findById(id);
        
        if (almoxarifadoOpt.isPresent()) {
            Almoxarifado almoxarifado = almoxarifadoOpt.get();
            
            // Verificar se há produtos associados ao almoxarifado
            List<Produto> produtos = produtoRepository.findByAlmoxarifado(almoxarifado);
            
            if (!produtos.isEmpty()) {
                throw new IllegalStateException("Não é possível excluir o almoxarifado pois existem produtos associados");
            }
            
            // Remover o almoxarifado
            almoxarifadoRepository.delete(almoxarifado);
        }
    }
}
