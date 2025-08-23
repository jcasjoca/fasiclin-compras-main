package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.FornecedorProduto;
import com.br.fasipe.compras.repository.FornecedorProdutoRepository;
import com.br.fasipe.compras.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;
    
    @Autowired
    private FornecedorProdutoRepository fornecedorProdutoRepository;
    
    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }
    
    public Optional<Fornecedor> buscarPorId(Integer id) {
        return fornecedorRepository.findById(id);
    }
    
    public Fornecedor buscarPorIdPessoa(Integer idPessoa) {
        return fornecedorRepository.findByIdPessoa(idPessoa);
    }
    
    @Transactional
    public Fornecedor salvar(Fornecedor fornecedor) {
        // Validações de negócio podem ser adicionadas aqui
        return fornecedorRepository.save(fornecedor);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findById(id);
        
        if (fornecedorOpt.isPresent()) {
            Fornecedor fornecedor = fornecedorOpt.get();
            
            // Verificar se o fornecedor possui produtos associados
            List<FornecedorProduto> fornecedorProdutos = fornecedorProdutoRepository.findByFornecedor(fornecedor);
            
            if (!fornecedorProdutos.isEmpty()) {
                throw new IllegalStateException("Não é possível excluir o fornecedor pois existem produtos associados");
            }
            
            // Remover o fornecedor
            fornecedorRepository.delete(fornecedor);
        }
    }
}
