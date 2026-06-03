package br.com.fiap.jornadaterra.repository;

import br.com.fiap.jornadaterra.model.Fazenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FazendaRepository extends JpaRepository<Fazenda, Long> {
    List<Fazenda> findByProdutorId(Long produtorId);
}
