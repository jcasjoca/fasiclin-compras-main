package com.br.fasipe.compras.service;

import com.br.fasipe.compras.model.Unimedida;
import com.br.fasipe.compras.repository.UnimedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnimedidaService {
    
    @Autowired
    private UnimedidaRepository unimedidaRepository;
    
    public List<Unimedida> findAll() {
        return unimedidaRepository.findAll();
    }
    
    public List<Unimedida> findAllOrderByDescricao() {
        return unimedidaRepository.findAllOrderByDescricao();
    }
    
    public Optional<Unimedida> findById(Integer id) {
        return unimedidaRepository.findById(id);
    }
    
    public Optional<Unimedida> findByUnidadeAbreviacao(String unidadeAbreviacao) {
        return unimedidaRepository.findByUnidadeAbreviacao(unidadeAbreviacao);
    }
    
    public List<Unimedida> findByDescricao(String descricao) {
        return unimedidaRepository.findByDescricaoContainingIgnoreCase(descricao);
    }
    
    public Unimedida save(Unimedida unimedida) {
        validateUnimedida(unimedida);
        return unimedidaRepository.save(unimedida);
    }
    
    public Unimedida update(Integer id, Unimedida unimedida) {
        Optional<Unimedida> existingUnimedida = unimedidaRepository.findById(id);
        if (existingUnimedida.isPresent()) {
            unimedida.setId(id);
            validateUnimedida(unimedida);
            return unimedidaRepository.save(unimedida);
        }
        throw new RuntimeException("Unidade de medida não encontrada com ID: " + id);
    }
    
    public void deleteById(Integer id) {
        if (unimedidaRepository.existsById(id)) {
            unimedidaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Unidade de medida não encontrada com ID: " + id);
        }
    }
    
    public boolean existsByUnidadeAbreviacao(String unidadeAbreviacao) {
        return unimedidaRepository.findByUnidadeAbreviacao(unidadeAbreviacao).isPresent();
    }
    
    public List<Unimedida> searchByDescricao(String termo) {
        return unimedidaRepository.findByDescricaoContaining(termo);
    }
    
    private void validateUnimedida(Unimedida unimedida) {
        if (unimedida.getDescricao() == null || unimedida.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        if (unimedida.getUnidadeAbreviacao() == null || unimedida.getUnidadeAbreviacao().trim().isEmpty()) {
            throw new IllegalArgumentException("Unidade de abreviação é obrigatória");
        }
        
        // Validar tamanho da abreviação
        if (unimedida.getUnidadeAbreviacao().length() > 3) {
            throw new IllegalArgumentException("Unidade de abreviação deve ter no máximo 3 caracteres");
        }
        
        // Verificar se a abreviação é única
        Optional<Unimedida> existingUnimedida = unimedidaRepository.findByUnidadeAbreviacao(unimedida.getUnidadeAbreviacao());
        if (existingUnimedida.isPresent() && !existingUnimedida.get().getId().equals(unimedida.getId())) {
            throw new IllegalArgumentException("Já existe uma unidade de medida com esta abreviação");
        }
        
        // Converter abreviação para maiúscula
        unimedida.setUnidadeAbreviacao(unimedida.getUnidadeAbreviacao().toUpperCase().trim());
        
        // Limpar espaços da descrição
        unimedida.setDescricao(unimedida.getDescricao().trim());
    }
}
