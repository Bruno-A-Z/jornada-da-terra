package br.com.fiap.jornadaterra.repository;

import br.com.fiap.jornadaterra.enums.StatusMissao;
import br.com.fiap.jornadaterra.model.missao.Missao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long> {
    List<Missao> findByFazendaId(Long fazendaId);
    List<Missao> findByFazendaIdAndStatus(Long fazendaId, StatusMissao status);
    List<Missao> findByFazendaProdutorId(Long produtorId);
}
