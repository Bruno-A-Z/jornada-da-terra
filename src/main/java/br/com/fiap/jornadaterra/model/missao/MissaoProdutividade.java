package br.com.fiap.jornadaterra.model.missao;

import br.com.fiap.jornadaterra.enums.TipoCultura;
import br.com.fiap.jornadaterra.model.Fazenda;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Missões voltadas a melhorar a produtividade da lavoura:
 * plantio, adubação, irrigação programada, colheita.
 *
 * Exemplo: "🌱 Índice NDVI baixo detectado! Plante as
 *           mudas de reposição no Setor Leste agora."
 */
@Entity
@DiscriminatorValue("PRODUTIVIDADE")
public class MissaoProdutividade extends Missao {

    @Enumerated(EnumType.STRING)
    @Column(name = "cultura")
    private TipoCultura cultura;

    // Meta de produtividade (sacas/hectare, por exemplo)
    @Column(name = "meta_produtividade")
    private double metaProdutividade;

    // Produtividade atual registrada
    @Column(name = "produtividade_atual")
    private double produtividadeAtual;

    // Tipo de ação necessária
    @Column(name = "tipo_acao")
    private String tipoAcao; // "PLANTIO", "ADUBACAO", "IRRIGACAO", "COLHEITA"

    // ===================== CONSTRUTORES =====================

    public MissaoProdutividade() {
        super();
    }

    public MissaoProdutividade(TipoCultura cultura, String tipoAcao,
                                double metaProdutividade, Fazenda fazenda) {
        super(
            gerarTitulo(cultura, tipoAcao),
            gerarDescricao(cultura, tipoAcao, metaProdutividade),
            gerarMensagemHeroica(cultura, tipoAcao),
            calcularPontos(tipoAcao),
            1,
            LocalDateTime.now().plusDays(7),
            fazenda
        );
        this.cultura = cultura;
        this.tipoAcao = tipoAcao;
        this.metaProdutividade = metaProdutividade;
        this.produtividadeAtual = 0;
    }

    // ===================== MÉTODOS ABSTRATOS IMPLEMENTADOS (Polimorfismo) =====================

    @Override
    public boolean validarConclusao() {
        // Produtividade atingiu ou superou a meta
        return produtividadeAtual >= metaProdutividade;
    }

    @Override
    public String getIcone() {
        return switch (cultura) {
            case SOJA    -> "🫘";
            case MILHO   -> "🌽";
            case CAFE    -> "☕";
            case TRIGO   -> "🌾";
            default      -> "🌱";
        };
    }

    @Override
    public String getCategoria() {
        return "Produtividade Agrícola";
    }

    // ===================== MÉTODOS AUXILIARES =====================

    private static String gerarTitulo(TipoCultura cultura, String acao) {
        return acao + " de " + cultura.name().replace("_", " ") + " - Missão da Terra";
    }

    private static String gerarDescricao(TipoCultura cultura, String acao, double meta) {
        return String.format("Realize o %s da cultura de %s para atingir a meta de %.1f sacas/hectare.",
                acao.toLowerCase(), cultura.name(), meta);
    }

    private static String gerarMensagemHeroica(TipoCultura cultura, String acao) {
        return switch (acao) {
            case "PLANTIO"   -> "A terra clama por sementes! O destino da colheita está em suas mãos!";
            case "ADUBACAO"  -> "Alimente a terra e ela te devolverá em abundância!";
            case "IRRIGACAO" -> "Cada gota conta! Dê vida à sua lavoura agora!";
            case "COLHEITA"  -> "É hora da glória! Recolha os frutos do seu trabalho, Guardião!";
            default          -> "A lavoura aguarda sua sabedoria, Mestre!";
        };
    }

    private static int calcularPontos(String acao) {
        return switch (acao) {
            case "COLHEITA"  -> 200;
            case "PLANTIO"   -> 150;
            case "ADUBACAO"  -> 100;
            case "IRRIGACAO" -> 80;
            default          -> 60;
        };
    }

    // ===================== GETTERS E SETTERS =====================

    public TipoCultura getCultura() { return cultura; }
    public void setCultura(TipoCultura cultura) { this.cultura = cultura; }

    public double getMetaProdutividade() { return metaProdutividade; }
    public void setMetaProdutividade(double metaProdutividade) { this.metaProdutividade = metaProdutividade; }

    public double getProdutividadeAtual() { return produtividadeAtual; }
    public void setProdutividadeAtual(double produtividadeAtual) { this.produtividadeAtual = produtividadeAtual; }

    public String getTipoAcao() { return tipoAcao; }
    public void setTipoAcao(String tipoAcao) { this.tipoAcao = tipoAcao; }
}
