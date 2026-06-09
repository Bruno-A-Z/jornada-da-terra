package br.com.fiap.jornadaterra.model.missao;

import br.com.fiap.jornadaterra.model.Fazenda;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Missões de rotina para manter a fazenda saudável:
 * verificação de sensores, atualização de dados, inspeção de setores.
 *
 * Exemplo: "📡 Atualize os dados do Setor Norte para receber
 *           previsões mais precisas do satélite."
 */

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Entity
@DiscriminatorValue("MONITORAMENTO")
public class MissaoMonitoramento extends Missao {

    // Número de setores que precisam ser verificados
    @Column(name = "setores_a_verificar")
    private int setoresAVerificar;

    // Quantos setores já foram verificados pelo produtor
    @Column(name = "setores_verificados")
    private int setoresVerificados;

    // Frequência: diária, semanal, mensal
    @Column(name = "frequencia")
    private String frequencia;

    // ===================== CONSTRUTORES =====================

    public MissaoMonitoramento() {
        super();
        this.setoresVerificados = 0;
    }

    public MissaoMonitoramento(int setoresAVerificar, String frequencia, Fazenda fazenda) {
        super(
            "Patrulha da Terra - " + fazenda.getNome(),
            "Monitore " + setoresAVerificar + " setores da fazenda e atualize os dados satelitais.",
            "🛰️ O satélite precisa de você! Faça a ronda e mantenha a fazenda no mapa!",
            50 * setoresAVerificar,
            1,
            calcularPrazo(frequencia),
            fazenda
        );
        this.setoresAVerificar = setoresAVerificar;
        this.frequencia = frequencia;
    }


    @Override
    public boolean validarConclusao() {
        // Missão concluída quando todos os setores forem verificados
        return setoresVerificados >= setoresAVerificar;
    }

    public String getIcone() {
        return "📡";
    }

    public String getCategoria() {
        return "Monitoramento Satelital";
    }

    // ===================== MÉTODOS DE NEGÓCIO =====================

    /**
     * Registra a verificação de um setor.
     * Demonstra lógica de progresso de missão.
     */
    public String verificarSetor(String nomeSetor) {
        if (setoresVerificados >= setoresAVerificar) {
            return "Todos os setores já foram verificados!";
        }
        setoresVerificados++;
        int restantes = setoresAVerificar - setoresVerificados;

        if (restantes == 0) {
            return "✅ " + nomeSetor + " verificado! Todos os setores concluídos!";
        }
        return "✅ " + nomeSetor + " verificado! Ainda faltam " + restantes + " setor(es).";
    }

    /**
     * Retorna o progresso em percentual
     */
    public double getProgressoPercentual() {
        if (setoresAVerificar == 0) return 0;
        return (double) setoresVerificados / setoresAVerificar * 100;
    }

    private static LocalDateTime calcularPrazo(String frequencia) {
        return switch (frequencia.toLowerCase()) {
            case "diaria"  -> LocalDateTime.now().plusDays(1);
            case "semanal" -> LocalDateTime.now().plusWeeks(1);
            case "mensal"  -> LocalDateTime.now().plusMonths(1);
            default        -> LocalDateTime.now().plusDays(3);
        };
    }
}
