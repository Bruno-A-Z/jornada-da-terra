package br.com.fiap.jornadaterra.repository;

import br.com.fiap.jornadaterra.model.Produtor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutorRepository extends JpaRepository<Produtor, Long> {
    Optional<Produtor> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
