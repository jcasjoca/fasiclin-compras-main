package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.model.UnidadeMedida;
import com.br.fasipe.compras.repository.ProdutoRepository;
import com.br.fasipe.compras.repository.UnidadeMedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadeMedidaService {

    @Autowired
    private UnidadeMedidaRepository unidadeMedidaRepository;
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    public List<UnidadeMedida> listarTodas() {
        return unidadeMedidaRepository.findAll();
    }
    
    public Optional<UnidadeMedida> buscarPorId(Integer id) {
        return unidadeMedidaRepository.findById(id);
    }
    
    public UnidadeMedida buscarPorAbreviacao(String abreviacao) {
        return unidadeMedidaRepository.findByAbreviacao(abreviacao);
    }
    
    public List<UnidadeMedida> buscarPorDescricao(String descricao) {
        return unidadeMedidaRepository.findByDescricaoContainingIgnoreCase(descricao);
    }
    
    @Transactional
    public UnidadeMedida salvar(UnidadeMedida unidadeMedida) {
        // Validações de negócio podem ser adicionadas aqui
        return unidadeMedidaRepository.save(unidadeMedida);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<UnidadeMedida> unidadeMedidaOpt = unidadeMedidaRepository.findById(id);
        
        if (unidadeMedidaOpt.isPresent()) {
            UnidadeMedida unidadeMedida = unidadeMedidaOpt.get();
            
            // Verificar se há produtos associados à unidade de medida
            List<Produto> produtos = produtoRepository.findAll();
            boolean emUso = produtos.stream()
                .anyMatch(p -> p.getUnidadeMedida() != null && 
                               p.getUnidadeMedida().getId().equals(unidadeMedida.getId()));
            
            if (emUso) {
                throw new IllegalStateException("Não é possível excluir a unidade de medida pois existem produtos associados");
            }
            
            // Remover a unidade de medida
            unidadeMedidaRepository.delete(unidadeMedida);
        }
    }
}
