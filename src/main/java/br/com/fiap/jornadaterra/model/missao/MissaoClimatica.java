package br.com.fiap.jornadaterra.model.missao;

import br.com.fiap.jornadaterra.enums.TipoAlerta;
import br.com.fiap.jornadaterra.model.Fazenda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Gerada automaticamente quando o sistema satelital detecta
 * riscos climáticos (geada, seca, chuva excessiva, etc).
 *
 * Exemplo: "⚠️ Alerta de geada se aproximando do setor sul:
 *           inicie a proteção da colheita!"
 */


@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIMATICA")
public class MissaoClimatica extends Missao {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta")
    private TipoAlerta tipoAlerta;

    // Nome do setor em risco
    @Column(name = "setor_afetado")
    private String setorAfetado;

    // Temperatura prevista (para missões de geada)
    @Column(name = "temperatura_prevista")
    private double temperaturaPrevista;

    // Flag que indica se o produtor tomou a ação necessária
    @Column(name = "acao_realizada")
    private boolean acaoRealizada;

    // ===================== CONSTRUTORES =====================

    public MissaoClimatica() {
        super();
        this.acaoRealizada = false;
    }

    public MissaoClimatica(TipoAlerta tipoAlerta, String setorAfetado,
                           double temperaturaPrevista, Fazenda fazenda) {
        super(
            gerarTitulo(tipoAlerta, setorAfetado),
            gerarDescricao(tipoAlerta, setorAfetado, temperaturaPrevista),
            gerarMensagemHeroica(tipoAlerta),
            calcularPontos(tipoAlerta),
            1,
            LocalDateTime.now().plusHours(24),
            fazenda
        );
        this.tipoAlerta = tipoAlerta;
        this.setorAfetado = setorAfetado;
        this.temperaturaPrevista = temperaturaPrevista;
    }



    @Override
    public boolean validarConclusao() {
        // Missão climática é concluída quando o produtor confirma a ação
        return acaoRealizada;
    }

    public String getIcone() {
        return switch (tipoAlerta) {
            case GEADA          -> "🥶";
            case SECA           -> "☀️";
            case CHUVA_EXCESSIVA -> "🌧️";
            case GRANIZO        -> "🌨️";
            case VENTO_FORTE    -> "💨";
            default             -> "🌤️";
        };
    }

    public String getCategoria() {
        return "Clima & Proteção";
    }


    private static String gerarTitulo(TipoAlerta alerta, String setor) {
        return switch (alerta) {
            case GEADA           -> "Defesa contra Geada - " + setor;
            case SECA            -> "Batalha contra a Seca - " + setor;
            case CHUVA_EXCESSIVA -> "Proteção contra Enchente - " + setor;
            case GRANIZO         -> "Escudo contra Granizo - " + setor;
            case VENTO_FORTE     -> "Resistência ao Vento - " + setor;
            default              -> "Monitoramento Climático - " + setor;
        };
    }

    private static String gerarDescricao(TipoAlerta alerta, String setor, double temp) {
        return switch (alerta) {
            case GEADA -> String.format(
                "Temperatura prevista de %.1f°C no %s. Ative os sistemas de proteção da lavoura.", temp, setor);
            case SECA  -> "Umidade do solo crítica detectada no " + setor + ". Inicie irrigação de emergência.";
            default    -> "Condição climática adversa detectada no " + setor + ". Tome as medidas preventivas.";
        };
    }

    private static String gerarMensagemHeroica(TipoAlerta alerta) {
        return switch (alerta) {
            case GEADA           -> "O inverno chegou! Proteja sua colheita ou perca tudo, Guardião!";
            case SECA            -> "A terra está com sede! Apenas você pode salvar a lavoura!";
            case CHUVA_EXCESSIVA -> "As águas avançam! Drene os campos antes que seja tarde!";
            case GRANIZO         -> "Pedras do céu se aproximam! Cubra suas plantações agora!";
            default              -> "A natureza desafia! Mostre que você é o Mestre da Terra!";
        };
    }

    private static int calcularPontos(TipoAlerta alerta) {
        return switch (alerta) {
            case GEADA, GRANIZO  -> 150; // Alertas mais críticos valem mais
            case SECA            -> 120;
            case CHUVA_EXCESSIVA -> 100;
            default              -> 80;
        };
    }


    public TipoAlerta getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(TipoAlerta tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getSetorAfetado() { return setorAfetado; }
    public void setSetorAfetado(String setorAfetado) { this.setorAfetado = setorAfetado; }

    public double getTemperaturaPrevista() { return temperaturaPrevista; }
    public void setTemperaturaPrevista(double temperaturaPrevista) { this.temperaturaPrevista = temperaturaPrevista; }

    public boolean isAcaoRealizada() { return acaoRealizada; }
    public void setAcaoRealizada(boolean acaoRealizada) { this.acaoRealizada = acaoRealizada; }
}
