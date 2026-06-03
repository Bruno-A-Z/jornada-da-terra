package br.com.fiap.jornadaterra.service;

import br.com.fiap.jornadaterra.exception.BusinessException;
import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.repository.ProdutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutorService {

    @Autowired
    private ProdutorRepository produtorRepository;

    @Transactional
    public Produtor cadastrar(Produtor produtor) {
        if (produtorRepository.existsByCpf(produtor.getCpf())) {
            throw new BusinessException("CPF já cadastrado: " + produtor.getCpf());
        }
        return produtorRepository.save(produtor);
    }

    public List<Produtor> listarTodos() {
        return produtorRepository.findAll();
    }

    public Optional<Produtor> buscarPorId(Long id) {
        return produtorRepository.findById(id);
    }

    public Optional<Produtor> buscarPorCpf(String cpf) {
        return produtorRepository.findByCpf(cpf);
    }

    @Transactional
    public Produtor atualizar(Long id, Produtor dadosAtualizados) {
        Produtor produtor = produtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado: " + id));

        produtor.setNome(dadosAtualizados.getNome());
        produtor.setEmail(dadosAtualizados.getEmail());
        produtor.setTelefone(dadosAtualizados.getTelefone());

        return produtorRepository.save(produtor);
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produtor não encontrado: " + id);
        }
        produtorRepository.deleteById(id);
    }
}
