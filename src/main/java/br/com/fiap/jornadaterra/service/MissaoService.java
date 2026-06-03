package br.com.fiap.jornadaterra.service;

import br.com.fiap.jornadaterra.enums.StatusMissao;
import br.com.fiap.jornadaterra.enums.TipoAlerta;
import br.com.fiap.jornadaterra.enums.TipoCultura;
import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.model.Setor;
import br.com.fiap.jornadaterra.model.missao.*;
import br.com.fiap.jornadaterra.repository.FazendaRepository;
import br.com.fiap.jornadaterra.repository.MissaoRepository;
import br.com.fiap.jornadaterra.repository.ProdutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE principal de missões.
 * Coordena a geração automática de missões e a interação do produtor.
 *
 * Demonstra: uso de polimorfismo (Missao abstrata com subtipos),
 * lógica de negócio encapsulada, e integração com gamificação.
 */
@Service
public class MissaoService {

    @Autowired
    private MissaoRepository missaoRepository;

    @Autowired
    private ProdutorRepository produtorRepository;

    @Autowired
    private FazendaRepository fazendaRepository;


    /**
     * Analisa os setores da fazenda e gera missões baseadas nos dados satelitais.
     * É o "motor" do sistema - transforma dados em missões.
     */
    @Transactional
    public List<Missao> gerarMissoesAutomaticas(Fazenda fazenda) {
        // Para cada setor com risco, gera uma missão climática
        for (Setor setor : fazenda.getSetores()) {
            if (setor.getNivelRisco() >= 3 && setor.getAlertaAtivo() != TipoAlerta.NORMAL) {

                // Verifica se já existe missão ativa para este setor
                boolean missaoAtiva = missaoRepository.findByFazendaIdAndStatus(
                        fazenda.getId(), StatusMissao.PENDENTE)
                        .stream()
                        .anyMatch(missao -> missao instanceof MissaoClimatica mc &&
                                mc.getSetorAfetado().equals(setor.getNome()));

                if (!missaoAtiva) {
                    MissaoClimatica missao = new MissaoClimatica(
                            setor.getAlertaAtivo(),
                            setor.getNome(),
                            setor.getTemperaturaAtual(),
                            fazenda
                    );
                    missaoRepository.save(missao);
                }
            }
        }

        // Gera missão de monitoramento semanal se não houver uma ativa
        boolean temMonitoramento = missaoRepository
                .findByFazendaIdAndStatus(fazenda.getId(), StatusMissao.PENDENTE)
                .stream()
                .anyMatch(missao -> missao instanceof MissaoMonitoramento);

        if (!temMonitoramento && !fazenda.getSetores().isEmpty()) {
            MissaoMonitoramento missaoMonit = new MissaoMonitoramento(
                    fazenda.getSetores().size(), "semanal", fazenda);
            missaoRepository.save(missaoMonit);
        }

        return missaoRepository.findByFazendaIdAndStatus(fazenda.getId(), StatusMissao.PENDENTE);
    }

    // ===================== INTERAÇÕES DO PRODUTOR =====================



    /**
     * Produtor inicia uma missão.
     * Demonstra polimorfismo: chama iniciar() na classe base.
     */
    @Transactional
    public String iniciarMissao(Long missaoId, Long produtorId) {
        Optional<Missao> missaoOpt = missaoRepository.findById(missaoId);
        if (missaoOpt.isEmpty()) return "❌ Missão não encontrada.";

        Missao missao = missaoOpt.get();

        Optional<Produtor> produtorOpt = produtorRepository.findById(produtorId);
        if (produtorOpt.isPresent()) {
            Produtor produtor = produtorOpt.get();
            if (produtor.getNivel() < missao.getNivelMinimoProdutor()) {
                return "⚠️ Você precisa ser nível " + missao.getNivelMinimoProdutor() +
                       " para esta missão. Seu nível atual: " + produtor.getNivel();
            }
        }

        String resultado = missao.iniciar();
        missaoRepository.save(missao);
        return resultado;
    }

    @Transactional
    public Missao criarMissaoProdutividade(Long fazendaId, String tipoAcao,
                                           String cultura, double meta) {
        Fazenda fazenda = fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new RuntimeException("Fazenda não encontrada: " + fazendaId));
        TipoCultura tipoCultura = TipoCultura.valueOf(cultura.toUpperCase());
        MissaoProdutividade missao = new MissaoProdutividade(tipoCultura, tipoAcao, meta, fazenda);
        return missaoRepository.save(missao);
    }

    /**
     * Produtor conclui uma missão e recebe pontos.
     * Polimorfismo: cada subclasse valida de forma diferente.
     */
    @Transactional
    public String concluirMissao(Long missaoId, Long produtorId) {
        Optional<Missao> missaoOpt = missaoRepository.findById(missaoId);
        Optional<Produtor> produtorOpt = produtorRepository.findById(produtorId);

        if (missaoOpt.isEmpty()) return "❌ Missão não encontrada.";
        if (produtorOpt.isEmpty()) return "❌ Produtor não encontrado.";

        Missao missao = missaoOpt.get();
        Produtor produtor = produtorOpt.get();

        String resultado = missao.concluir();

        if (missao.getStatus() == StatusMissao.CONCLUIDA) {
            // Adiciona pontos ao produtor (gamificação)
            String feedback = produtor.adicionarPontos(missao.getPontosRecompensa());
            produtorRepository.save(produtor);
            missaoRepository.save(missao);
            return resultado + "\n" + feedback;
        }

        missaoRepository.save(missao);
        return resultado;
    }

    /**
     * Confirma ação numa missão climática (proteção da colheita, irrigação, etc)
     */
    @Transactional
    public String confirmarAcaoClimatica(Long missaoId) {
        Optional<Missao> missaoOpt = missaoRepository.findById(missaoId);
        if (missaoOpt.isEmpty()) return "❌ Missão não encontrada.";

        Missao missao = missaoOpt.get();

        if (missao instanceof MissaoClimatica mc) {
            mc.setAcaoRealizada(true);
            missaoRepository.save(mc);
            return "✅ Ação confirmada! Agora conclua a missão para ganhar seus pontos.";
        }

        return "❌ Esta missão não é do tipo climática.";
    }

    /**
     * Registra verificação de setor numa missão de monitoramento
     */
    @Transactional
    public String verificarSetorMonitoramento(Long missaoId, String nomeSetor) {
        Optional<Missao> missaoOpt = missaoRepository.findById(missaoId);
        if (missaoOpt.isEmpty()) return "❌ Missão não encontrada.";

        Missao missao = missaoOpt.get();

        if (missao instanceof MissaoMonitoramento mm) {
            String resultado = mm.verificarSetor(nomeSetor);
            missaoRepository.save(mm);
            return resultado + String.format(" (%.0f%% concluído)", mm.getProgressoPercentual());
        }

        return "❌ Esta missão não é do tipo monitoramento.";
    }

    @Transactional
    public String registrarProdutividade(Long missaoId, double produtividadeAtual) {
        Missao missao = missaoRepository.findById(missaoId)
                .orElseThrow(() -> new RuntimeException("Missão não encontrada: " + missaoId));
        if (missao instanceof MissaoProdutividade mp) {
            mp.setProdutividadeAtual(produtividadeAtual);
            missaoRepository.save(mp);
            return String.format("📊 Produtividade registrada: %.1f / %.1f sacas/ha (%.0f%%)",
                    produtividadeAtual, mp.getMetaProdutividade(),
                    produtividadeAtual / mp.getMetaProdutividade() * 100);
        }
        return "❌ Esta missão não é do tipo produtividade.";
    }


    public List<Missao> listarMissoesPendentes(Long produtorId) {
        return missaoRepository.findByFazendaProdutorId(produtorId)
                .stream()
                .filter(m -> m.getStatus() == StatusMissao.PENDENTE ||
                             m.getStatus() == StatusMissao.EM_ANDAMENTO)
                .toList();
    }

    public List<Missao> listarTodasMissoes(Long produtorId) {
        return missaoRepository.findByFazendaProdutorId(produtorId);
    }

    public Optional<Missao> buscarPorId(Long id) {
        return missaoRepository.findById(id);
    }
}
