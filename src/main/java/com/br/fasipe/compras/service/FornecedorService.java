package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Fornecedor;
import com.br.fasipe.compras.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {
    
    @Autowired
    private FornecedorRepository fornecedorRepository;
    
    public List<Fornecedor> findAll() {
        return fornecedorRepository.findAll();
    }
    
    public Optional<Fornecedor> findById(Integer id) {
        return fornecedorRepository.findById(id);
    }
    
    public Optional<Fornecedor> findByIdPessoa(Integer idPessoa) {
        return fornecedorRepository.findByIdPessoa(idPessoa);
    }
    
    public Fornecedor save(Fornecedor fornecedor) {
        validateFornecedor(fornecedor);
        return fornecedorRepository.save(fornecedor);
    }
    
    public Fornecedor update(Integer id, Fornecedor fornecedor) {
        Optional<Fornecedor> existingFornecedor = fornecedorRepository.findById(id);
        if (existingFornecedor.isPresent()) {
            fornecedor.setId(id);
            validateFornecedor(fornecedor);
            return fornecedorRepository.save(fornecedor);
        }
        throw new RuntimeException("Fornecedor não encontrado com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (fornecedorRepository.existsById(id)) {
            fornecedorRepository.deleteById(id);
        } else {
            throw new RuntimeException("Fornecedor não encontrado com ID: " + id);
        }
    }
    
    public List<Fornecedor> findByRepresentante(String representante) {
        return fornecedorRepository.findByRepresentanteContaining(representante);
    }
    
    public List<Fornecedor> findByDescricao(String descricao) {
        return fornecedorRepository.findByDescricaoContaining(descricao);
    }
    
    private void validateFornecedor(Fornecedor fornecedor) {
        if (fornecedor.getIdPessoa() == null) {
            throw new IllegalArgumentException("ID da pessoa é obrigatório");
        }
        
        // Verificar se já existe fornecedor com mesmo idPessoa (exceto se for o próprio)
        Optional<Fornecedor> existingFornecedor = fornecedorRepository.findByIdPessoa(fornecedor.getIdPessoa());
        if (existingFornecedor.isPresent() && !existingFornecedor.get().getId().equals(fornecedor.getId())) {
            throw new IllegalArgumentException("Já existe um fornecedor com este ID de pessoa");
        }
    }
}
