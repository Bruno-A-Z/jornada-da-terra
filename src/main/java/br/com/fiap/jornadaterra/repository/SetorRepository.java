package br.com.fiap.jornadaterra.repository;

import br.com.fiap.jornadaterra.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetorRepository extends JpaRepository<Setor, Long> {
    List<Setor> findByFazendaId(Long fazendaId);
}