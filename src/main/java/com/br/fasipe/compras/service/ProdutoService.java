package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Almoxarifado;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.EstoqueRepository;
import com.br.fasipe.compras.repository.ItemOrdemCompraRepository;
import com.br.fasipe.compras.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private EstoqueRepository estoqueRepository;
    
    @Autowired
    private ItemOrdemCompraRepository itemOrdemCompraRepository;
    
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
    
    public Optional<Produto> buscarPorId(Integer id) {
        return produtoRepository.findById(id);
    }
    
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Produto> buscarPorAlmoxarifado(Almoxarifado almoxarifado) {
        return produtoRepository.findByAlmoxarifado(almoxarifado);
    }
    
    public List<Produto> buscarProdutosComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo();
    }
    
    @Transactional
    public Produto salvar(Produto produto) {
        // Validações de negócio podem ser adicionadas aqui
        return produtoRepository.save(produto);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            
            // Verificar se o produto está em uso em algum estoque ou ordem de compra
            Integer quantidadeEstoque = estoqueRepository.findQuantidadeTotalByProduto(produto);
            if (quantidadeEstoque != null && quantidadeEstoque > 0) {
                throw new IllegalStateException("Não é possível excluir o produto pois existem registros de estoque associados");
            }
            
            // Verificar se o produto está em alguma ordem de compra
            if (!itemOrdemCompraRepository.findByProduto(produto).isEmpty()) {
                throw new IllegalStateException("Não é possível excluir o produto pois existem ordens de compra associadas");
            }
            
            // Remover o produto
            produtoRepository.delete(produto);
        }
    }
    
    public Integer obterQuantidadeEmEstoque(Integer produtoId) {
        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Integer quantidade = estoqueRepository.findQuantidadeTotalByProduto(produto);
            return quantidade != null ? quantidade : 0;
        }
        
        return 0;
    }
    
    public boolean verificarNecessidadeCompra(Integer produtoId) {
        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            Integer quantidadeAtual = estoqueRepository.findQuantidadeTotalByProduto(produto);
            
            if (quantidadeAtual == null) {
                quantidadeAtual = 0;
            }
            
            // Verificar se a quantidade está abaixo do ponto de pedido
            return quantidadeAtual <= produto.getPontoPedido();
        }
        
        return false;
    }
}
