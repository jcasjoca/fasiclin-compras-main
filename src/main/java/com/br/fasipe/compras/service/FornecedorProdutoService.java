package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.model.FornecedorProduto;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.FornecedorProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorProdutoService {

    @Autowired
    private FornecedorProdutoRepository fornecedorProdutoRepository;
    
    public List<FornecedorProduto> listarTodos() {
        return fornecedorProdutoRepository.findAll();
    }
    
    public Optional<FornecedorProduto> buscarPorId(Integer id) {
        return fornecedorProdutoRepository.findById(id);
    }
    
    public List<FornecedorProduto> buscarPorFornecedor(Fornecedor fornecedor) {
        return fornecedorProdutoRepository.findByFornecedor(fornecedor);
    }
    
    public List<FornecedorProduto> buscarPorProduto(Produto produto) {
        return fornecedorProdutoRepository.findByProduto(produto);
    }
    
    @Transactional
    public FornecedorProduto salvar(FornecedorProduto fornecedorProduto) {
        // Validações de negócio podem ser adicionadas aqui
        return fornecedorProdutoRepository.save(fornecedorProduto);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<FornecedorProduto> fornecedorProdutoOpt = fornecedorProdutoRepository.findById(id);
        
        if (fornecedorProdutoOpt.isPresent()) {
            FornecedorProduto fornecedorProduto = fornecedorProdutoOpt.get();
            fornecedorProdutoRepository.delete(fornecedorProduto);
        }
    }
    
    @Transactional
    public void associarProdutoAFornecedor(Produto produto, Fornecedor fornecedor) {
        // Verificar se já existe associação
        List<FornecedorProduto> associacoes = fornecedorProdutoRepository.findByProduto(produto);
        
        boolean associacaoExistente = associacoes.stream()
            .anyMatch(fp -> fp.getFornecedor().getId().equals(fornecedor.getId()));
            
        if (!associacaoExistente) {
            FornecedorProduto fornecedorProduto = new FornecedorProduto();
            fornecedorProduto.setFornecedor(fornecedor);
            fornecedorProduto.setProduto(produto);
            fornecedorProdutoRepository.save(fornecedorProduto);
        }
    }
    
    @Transactional
    public void desassociarProdutoDeFornecedor(Produto produto, Fornecedor fornecedor) {
        List<FornecedorProduto> associacoes = fornecedorProdutoRepository.findByProduto(produto);
        
        associacoes.stream()
            .filter(fp -> fp.getFornecedor().getId().equals(fornecedor.getId()))
            .findFirst()
            .ifPresent(fornecedorProdutoRepository::delete);
    }
}
