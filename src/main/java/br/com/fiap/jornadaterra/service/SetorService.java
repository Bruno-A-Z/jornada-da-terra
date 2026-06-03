package br.com.fiap.jornadaterra.service;

import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Setor;
import br.com.fiap.jornadaterra.repository.FazendaRepository;
import br.com.fiap.jornadaterra.repository.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SetorService {

    @Autowired
    private SetorRepository setorRepository;

    @Autowired
    private FazendaRepository fazendaRepository;

    @Transactional
    public Setor cadastrar(Setor setor, Long fazendaId) {
        Fazenda fazenda = fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new RuntimeException("Fazenda não encontrada: " + fazendaId));
        setor.setFazenda(fazenda);
        return setorRepository.save(setor);
    }

    public List<Setor> listarPorFazenda(Long fazendaId) {
        return setorRepository.findByFazendaId(fazendaId);
    }

    public Optional<Setor> buscarPorId(Long id) {
        return setorRepository.findById(id);
    }

    @Transactional
    public Setor atualizarDadosSatelitais(Long id, double temperatura,
                                          double umidade, double ndvi) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado: " + id));
        setor.atualizarDadosSatelitais(temperatura, umidade, ndvi);
        return setorRepository.save(setor);
    }

    @Transactional
    public Setor atualizar(Long id, Setor dados) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado: " + id));
        setor.setNome(dados.getNome());
        setor.setAreaHectares(dados.getAreaHectares());
        return setorRepository.save(setor);
    }

    @Transactional
    public void deletar(Long id) {
        if (!setorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Setor não encontrado: " + id);
        }
        setorRepository.deleteById(id);
    }
}