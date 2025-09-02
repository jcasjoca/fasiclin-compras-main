package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.ProdutoRepository;
import com.br.fasipe.compras.repository.UnimedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private UnimedidaRepository unimedidaRepository;
    
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }
    
    public Optional<Produto> findById(Integer id) {
        return produtoRepository.findById(id);
    }
    
    public List<Produto> findByNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Produto> findByDescricao(String descricao) {
        return produtoRepository.findByDescricaoContainingIgnoreCase(descricao);
    }
    
    public Optional<Produto> findByCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarras(codigoBarras);
    }
    
    public List<Produto> findByIdAlmoxarifado(Integer idAlmoxarifado) {
        return produtoRepository.findByIdAlmoxarifado(idAlmoxarifado);
    }
    
    public Produto save(Produto produto) {
        validateProduto(produto);
        return produtoRepository.save(produto);
    }
    
    public Produto update(Integer id, Produto produto) {
        Optional<Produto> existingProduto = produtoRepository.findById(id);
        if (existingProduto.isPresent()) {
            produto.setId(id);
            validateProduto(produto);
            return produtoRepository.save(produto);
        }
        throw new RuntimeException("Produto não encontrado com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
    }
    
    public List<Produto> findProdutosBaixoEstoque() {
        return produtoRepository.findProdutosBaixoEstoque();
    }
    
    public List<Produto> findByTemperaturaIdeal(BigDecimal tempMin, BigDecimal tempMax) {
        return produtoRepository.findByTemperaturaIdealBetween(tempMin, tempMax);
    }
    
    public List<Produto> findByEstoqueMaximoMenorQue(Integer limite) {
        return produtoRepository.findByEstoqueMaximoMenorQue(limite);
    }
    
    public List<Produto> findByEstoqueMinimoMaiorQue(Integer limite) {
        return produtoRepository.findByEstoqueMinimoMaiorQue(limite);
    }
    
    public boolean isProdutoComBaixoEstoque(Produto produto) {
        return produto.getEstoqueMinimo() >= produto.getPontoPedido();
    }
    
    public boolean isProdutoComEstoqueCritico(Produto produto) {
        return produto.getEstoqueMinimo() > produto.getPontoPedido();
    }
    
    public List<Produto> findProdutosPorUnidadeMedida(Integer unidadeMedidaId) {
        return unimedidaRepository.findById(unidadeMedidaId)
                .map(produtoRepository::findByUnidadeMedida)
                .orElse(List.of());
    }
    
    private void validateProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (produto.getDescricao() == null || produto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto é obrigatória");
        }
        
        if (produto.getUnidadeMedida() == null || produto.getUnidadeMedida().getId() == null) {
            throw new IllegalArgumentException("Unidade de medida é obrigatória");
        }
        
        if (produto.getEstoqueMaximo() == null || produto.getEstoqueMaximo() <= 0) {
            throw new IllegalArgumentException("Estoque máximo deve ser maior que zero");
        }
        
        if (produto.getEstoqueMinimo() == null || produto.getEstoqueMinimo() < 0) {
            throw new IllegalArgumentException("Estoque mínimo não pode ser negativo");
        }
        
        if (produto.getPontoPedido() == null || produto.getPontoPedido() < 0) {
            throw new IllegalArgumentException("Ponto de pedido não pode ser negativo");
        }
        
        // Validar se estoque mínimo não é maior que estoque máximo
        if (produto.getEstoqueMinimo() > produto.getEstoqueMaximo()) {
            throw new IllegalArgumentException("Estoque mínimo não pode ser maior que estoque máximo");
        }
        
        // Validar se ponto de pedido não é maior que estoque máximo
        if (produto.getPontoPedido() > produto.getEstoqueMaximo()) {
            throw new IllegalArgumentException("Ponto de pedido não pode ser maior que estoque máximo");
        }
        
        // Verificar se a unidade de medida existe
        if (!unimedidaRepository.existsById(produto.getUnidadeMedida().getId())) {
            throw new IllegalArgumentException("Unidade de medida não encontrada");
        }
        
        // Verificar se código de barras é único (se informado)
        if (produto.getCodigoBarras() != null && !produto.getCodigoBarras().trim().isEmpty()) {
            Optional<Produto> existingProduto = produtoRepository.findByCodigoBarras(produto.getCodigoBarras());
            if (existingProduto.isPresent() && !existingProduto.get().getId().equals(produto.getId())) {
                throw new IllegalArgumentException("Já existe um produto com este código de barras");
            }
        }
        
        // Validar temperatura ideal se informada
        if (produto.getTemperaturaIdeal() != null) {
            if (produto.getTemperaturaIdeal().compareTo(new BigDecimal("-50")) < 0 || 
                produto.getTemperaturaIdeal().compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("Temperatura ideal deve estar entre -50°C e 100°C");
            }
        }
    }
}
