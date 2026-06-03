package br.com.fiap.jornadaterra.service;

import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.repository.FazendaRepository;
import br.com.fiap.jornadaterra.repository.ProdutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FazendaService {

    @Autowired
    private FazendaRepository fazendaRepository;

    @Autowired
    private ProdutorRepository produtorRepository;

    @Transactional
    public Fazenda cadastrar(Fazenda fazenda, Long produtorId) {
        Produtor produtor = produtorRepository.findById(produtorId)
                .orElseThrow(() -> new RuntimeException("Produtor não encontrado: " + produtorId));
        fazenda.setProdutor(produtor);
        return fazendaRepository.save(fazenda);
    }

    @Transactional
    public Fazenda vincularProdutor(Long fazendaId, Long produtorId) {
        Fazenda fazenda = fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new RuntimeException("Fazenda não encontrada: " + fazendaId));
        Produtor produtor = produtorRepository.findById(produtorId)
                .orElseThrow(() -> new RuntimeException("Produtor não encontrado: " + produtorId));
        fazenda.setProdutor(produtor);
        return fazendaRepository.save(fazenda);
    }

    public List<Fazenda> listarPorProdutorId(Long produtorId) {
        return fazendaRepository.findByProdutorId(produtorId);
    }

    public Optional<Fazenda> buscarPorId(Long id) {
        return fazendaRepository.findById(id);
    }

    @Transactional
    public Fazenda atualizar(Long id, Fazenda dados) {
        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fazenda não encontrada: " + id));
        fazenda.setNome(dados.getNome());
        fazenda.setMunicipio(dados.getMunicipio());
        fazenda.setEstado(dados.getEstado());
        fazenda.setAreaHectares(dados.getAreaHectares());
        fazenda.setLatitude(dados.getLatitude());
        fazenda.setLongitude(dados.getLongitude());
        fazenda.setTipoCultura(dados.getTipoCultura());
        return fazendaRepository.save(fazenda);
    }

    @Transactional
    public void deletar(Long id) {
        if (!fazendaRepository.existsById(id)) {
            throw new RuntimeException("Fazenda não encontrada: " + id);
        }
        fazendaRepository.deleteById(id);
    }
}