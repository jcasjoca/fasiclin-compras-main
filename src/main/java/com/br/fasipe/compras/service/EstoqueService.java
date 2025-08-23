package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Estoque;
import com.br.fasipe.compras.model.Lote;
import com.br.fasipe.compras.model.Movimentacao;
import com.br.fasipe.compras.model.Produto;
import com.br.fasipe.compras.repository.EstoqueRepository;
import com.br.fasipe.compras.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;
    
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    
    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }
    
    public Optional<Estoque> buscarPorId(Integer id) {
        return estoqueRepository.findById(id);
    }
    
    public List<Estoque> buscarPorProduto(Produto produto) {
        return estoqueRepository.findByProduto(produto);
    }
    
    public List<Estoque> buscarPorLote(Lote lote) {
        return estoqueRepository.findByLote(lote);
    }
    
    public Integer obterQuantidadeTotalPorProduto(Produto produto) {
        Integer quantidade = estoqueRepository.findQuantidadeTotalByProduto(produto);
        return quantidade != null ? quantidade : 0;
    }
    
    @Transactional
    public Estoque salvar(Estoque estoque) {
        // Validações de negócio podem ser adicionadas aqui
        return estoqueRepository.save(estoque);
    }
    
    @Transactional
    public void excluir(Integer id) {
        Optional<Estoque> estoqueOpt = estoqueRepository.findById(id);
        
        if (estoqueOpt.isPresent()) {
            Estoque estoque = estoqueOpt.get();
            
            // Verificar se há movimentações associadas ao estoque
            List<Movimentacao> movimentacoes = movimentacaoRepository.findByEstoque(estoque);
            
            if (!movimentacoes.isEmpty()) {
                throw new IllegalStateException("Não é possível excluir o estoque pois existem movimentações associadas");
            }
            
            // Remover o estoque
            estoqueRepository.delete(estoque);
        }
    }
    
    @Transactional
    public void adicionarEstoque(Produto produto, Lote lote, Integer quantidade, Integer idUsuario, Integer idSetorOrigem) {
        // Verificar se já existe um registro de estoque para este produto e lote
        List<Estoque> estoques = estoqueRepository.findByLote(lote);
        Estoque estoque;
        
        if (!estoques.isEmpty()) {
            // Atualizar o estoque existente
            estoque = estoques.get(0);
            estoque.setQuantidadeEstoque(estoque.getQuantidadeEstoque() + quantidade);
        } else {
            // Criar um novo registro de estoque
            estoque = new Estoque();
            estoque.setProduto(produto);
            estoque.setLote(lote);
            estoque.setQuantidadeEstoque(quantidade);
        }
        
        // Salvar o estoque
        estoqueRepository.save(estoque);
        
        // Registrar a movimentação de entrada
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setEstoque(estoque);
        movimentacao.setIdUsuario(idUsuario);
        movimentacao.setIdSetorOrigem(idSetorOrigem);
        movimentacao.setQuantidadeMovimentada(quantidade);
        movimentacao.setDataMovimentacao(LocalDate.now());
        movimentacao.setTipoMovimentacao(Movimentacao.TipoMovimentacao.ENTRADA);
        
        movimentacaoRepository.save(movimentacao);
    }
    
    @Transactional
    public void retirarEstoque(Produto produto, Integer quantidade, Integer idUsuario, Integer idSetorOrigem, Integer idSetorDestino) {
        // Buscar todos os estoques do produto
        List<Estoque> estoques = estoqueRepository.findByProduto(produto);
        
        if (estoques.isEmpty()) {
            throw new IllegalStateException("Não há estoque disponível para este produto");
        }
        
        // Verificar se há quantidade suficiente
        Integer quantidadeTotal = estoqueRepository.findQuantidadeTotalByProduto(produto);
        
        if (quantidadeTotal == null || quantidadeTotal < quantidade) {
            throw new IllegalStateException("Quantidade em estoque insuficiente");
        }
        
        // Retirar do estoque usando FIFO (primeiro a entrar, primeiro a sair)
        int quantidadeRestante = quantidade;
        
        for (Estoque estoque : estoques) {
            if (quantidadeRestante <= 0) {
                break;
            }
            
            int quantidadeRetirada = Math.min(estoque.getQuantidadeEstoque(), quantidadeRestante);
            estoque.setQuantidadeEstoque(estoque.getQuantidadeEstoque() - quantidadeRetirada);
            estoqueRepository.save(estoque);
            
            // Registrar a movimentação de saída
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setEstoque(estoque);
            movimentacao.setIdUsuario(idUsuario);
            movimentacao.setIdSetorOrigem(idSetorOrigem);
            movimentacao.setIdSetorDestino(idSetorDestino);
            movimentacao.setQuantidadeMovimentada(quantidadeRetirada);
            movimentacao.setDataMovimentacao(LocalDate.now());
            movimentacao.setTipoMovimentacao(Movimentacao.TipoMovimentacao.SAIDA);
            
            movimentacaoRepository.save(movimentacao);
            
            quantidadeRestante -= quantidadeRetirada;
        }
    }
}
