package br.com.fiap.jornadaterra.model;

import br.com.fiap.jornadaterra.enums.TipoAlerta;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Setor geográfico da fazenda (ex: "Setor Sul", "Setor Norte").
 * Representa uma área monitorada por satélite individualmente.
 */
@Entity
@Table(name = "setores")
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // ex: "Setor Sul", "Setor Norte"

    private double areaHectares;

    // Nível de risco de 0 (normal) a 5 (crítico) - dados do satélite
    private int nivelRisco;

    // Temperatura atual em Celsius (dados satelitais/meteorológicos)
    private double temperaturaAtual;

    // Umidade do solo em percentual
    private double umidadeSolo;

    // Índice de vegetação (NDVI) - quanto mais próximo de 1, mais saudável
    private double indiceVegetacao;

    @Enumerated(EnumType.STRING)
    private TipoAlerta alertaAtivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id", nullable = false)
    @JsonBackReference
    private Fazenda fazenda;

    // ===================== CONSTRUTORES =====================

    public Setor() {
        this.alertaAtivo = TipoAlerta.NORMAL;
        this.nivelRisco = 0;
    }

    public Setor(String nome, double areaHectares, Fazenda fazenda) {
        this();
        this.nome = nome;
        this.areaHectares = areaHectares;
        this.fazenda = fazenda;
    }

    // ===================== MÉTODOS DE NEGÓCIO =====================

    /**
     * Atualiza dados satelitais e recalcula nível de risco automaticamente.
     * Demonstra lógica de negócio encapsulada no modelo.
     */
    public void atualizarDadosSatelitais(double temperatura, double umidade, double ndvi) {
        this.temperaturaAtual = temperatura;
        this.umidadeSolo = umidade;
        this.indiceVegetacao = ndvi;
        this.nivelRisco = calcularNivelRisco();
        this.alertaAtivo = determinarAlerta();
    }

    /**
     * Calcula o nível de risco baseado nos dados ambientais.
     * Usa if/else encadeado conforme estudado no questionário.
     */
    private int calcularNivelRisco() {
        int risco = 0;

        // Risco de geada
        if (temperaturaAtual < 2) {
            risco += 3;
        } else if (temperaturaAtual < 5) {
            risco += 2;
        }

        // Risco de seca
        if (umidadeSolo < 20) {
            risco += 2;
        } else if (umidadeSolo < 35) {
            risco += 1;
        }

        // Vegetação degradada
        if (indiceVegetacao < 0.2) {
            risco += 2;
        } else if (indiceVegetacao < 0.4) {
            risco += 1;
        }

        // Limita entre 0 e 5
        return Math.min(risco, 5);
    }

    /**
     * Determina o tipo de alerta baseado nas condições climáticas.
     */
    private TipoAlerta determinarAlerta() {
        if (temperaturaAtual < 2) return TipoAlerta.GEADA;
        if (umidadeSolo < 20)    return TipoAlerta.SECA;
        if (umidadeSolo > 90)    return TipoAlerta.CHUVA_EXCESSIVA;
        return TipoAlerta.NORMAL;
    }


    public String getDescricaoStatus() {
        return switch (nivelRisco) {
            case 0, 1 -> "✅ " + nome + " está saudável";
            case 2    -> "⚠️ " + nome + " requer atenção";
            case 3    -> "🔶 " + nome + " em situação de risco";
            case 4    -> "🔴 " + nome + " em perigo!";
            case 5    -> "🚨 " + nome + " em situação crítica!";
            default   -> "Status desconhecido";
        };
    }

    // ===================== GETTERS E SETTERS =====================

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getAreaHectares() { return areaHectares; }
    public void setAreaHectares(double areaHectares) { this.areaHectares = areaHectares; }

    public int getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(int nivelRisco) { this.nivelRisco = nivelRisco; }

    public double getTemperaturaAtual() { return temperaturaAtual; }
    public void setTemperaturaAtual(double temperaturaAtual) { this.temperaturaAtual = temperaturaAtual; }

    public double getUmidadeSolo() { return umidadeSolo; }
    public void setUmidadeSolo(double umidadeSolo) { this.umidadeSolo = umidadeSolo; }

    public double getIndiceVegetacao() { return indiceVegetacao; }
    public void setIndiceVegetacao(double indiceVegetacao) { this.indiceVegetacao = indiceVegetacao; }

    public TipoAlerta getAlertaAtivo() { return alertaAtivo; }
    public void setAlertaAtivo(TipoAlerta alertaAtivo) { this.alertaAtivo = alertaAtivo; }

    public Fazenda getFazenda() { return fazenda; }
    public void setFazenda(Fazenda fazenda) { this.fazenda = fazenda; }
}
