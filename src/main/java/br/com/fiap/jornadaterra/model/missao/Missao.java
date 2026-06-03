package br.com.fiap.jornadaterra.model.missao;

import br.com.fiap.jornadaterra.enums.StatusMissao;
import br.com.fiap.jornadaterra.model.Fazenda;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "missoes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_missao")
public abstract class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Column(name = "mensagem_heroica", length = 500)
    private String mensagemHeroica;

    @Enumerated(EnumType.STRING)
    private StatusMissao status;

    private int pontosRecompensa;
    private int nivelMinimoProdutor;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_limite")
    private LocalDateTime dataLimite;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id")
    private Fazenda fazenda;

    // ===================== CONSTRUTOR =====================

    public Missao() {
        this.status = StatusMissao.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    public Missao(String titulo, String descricao, String mensagemHeroica,
                  int pontosRecompensa, int nivelMinimoProdutor,
                  LocalDateTime dataLimite, Fazenda fazenda) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.mensagemHeroica = mensagemHeroica;
        this.pontosRecompensa = pontosRecompensa;
        this.nivelMinimoProdutor = nivelMinimoProdutor;
        this.dataLimite = dataLimite;
        this.fazenda = fazenda;
    }

    // ===================== MÉTODOS ABSTRATOS (Polimorfismo) =====================

    /**
     * Cada tipo de missão define sua própria lógica de conclusão.
     * POLIMORFISMO: mesmo método, comportamentos diferentes.
     */
    public abstract boolean validarConclusao();

    /**
     * Retorna o ícone/emoji representativo da missão no app.
     */
    public abstract String getIcone();

    /**
     * Retorna a categoria da missão para exibição.
     */
    public abstract String getCategoria();

    // ===================== MÉTODOS CONCRETOS COMPARTILHADOS =====================

    /**
     * Inicia a missão - muda status de PENDENTE para EM_ANDAMENTO
     */
    public String iniciar() {
        if (status != StatusMissao.PENDENTE) {
            return "Missão já foi iniciada ou finalizada.";
        }
        this.status = StatusMissao.EM_ANDAMENTO;
        return "⚔️ Missão '" + titulo + "' iniciada! " + mensagemHeroica;
    }

    /**
     * Tenta concluir a missão - chama o método abstrato de cada subclasse
     */
    public String concluir() {
        if (status != StatusMissao.EM_ANDAMENTO) {
            return "Missão não está em andamento.";
        }

        if (validarConclusao()) {
            this.status = StatusMissao.CONCLUIDA;
            this.dataConclusao = LocalDateTime.now();
            return "🏆 Missão '" + titulo + "' concluída! +" + pontosRecompensa + " pontos!";
        } else {
            this.status = StatusMissao.FALHA;
            return "❌ Missão '" + titulo + "' falhou. Tente novamente.";
        }
    }

    /**
     * Verifica se a missão expirou
     */
    public boolean isExpirada() {
        if (dataLimite == null) return false;
        return LocalDateTime.now().isAfter(dataLimite) && status == StatusMissao.PENDENTE;
    }

    // ===================== GETTERS E SETTERS =====================

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getMensagemHeroica() { return mensagemHeroica; }
    public void setMensagemHeroica(String mensagemHeroica) { this.mensagemHeroica = mensagemHeroica; }

    public StatusMissao getStatus() { return status; }
    public void setStatus(StatusMissao status) { this.status = status; }

    public int getPontosRecompensa() { return pontosRecompensa; }
    public void setPontosRecompensa(int pontosRecompensa) { this.pontosRecompensa = pontosRecompensa; }

    public int getNivelMinimoProdutor() { return nivelMinimoProdutor; }
    public void setNivelMinimoProdutor(int nivelMinimoProdutor) { this.nivelMinimoProdutor = nivelMinimoProdutor; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDateTime dataLimite) { this.dataLimite = dataLimite; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }

    public Fazenda getFazenda() { return fazenda; }
    public void setFazenda(Fazenda fazenda) { this.fazenda = fazenda; }
}
