package br.com.fiap.jornadaterra.model;

import br.com.fiap.jornadaterra.enums.TipoCultura;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o produtor rural - usuário principal do sistema.
 * Demonstra: Encapsulamento (getters/setters), Construtor, e relacionamento com Fazenda.
 */
@Entity
@Table(name = "produtores")
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Column(unique = true, nullable = false)
    private String cpf;

    @Email(message = "E-mail inválido")
    private String email;

    private String telefone;

    // Gamificação: pontos acumulados ao completar missões
    private int pontos;

    // Nível do produtor na jornada (1 = Semeador, 5 = Mestre da Terra)
    private int nivel;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    // Relacionamento: um produtor pode ter várias fazendas
    @OneToMany(mappedBy = "produtor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Fazenda> fazendas = new ArrayList<>();

    // ===================== CONSTRUTORES =====================

    // Construtor padrão exigido pelo JPA
    public Produtor() {
        this.pontos = 0;
        this.nivel = 1;
        this.dataCadastro = LocalDateTime.now();
    }

    // Construtor completo - aplica conceito visto na questão do questionário
    public Produtor(String nome, String cpf, String email, String telefone) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }



    /**
     * Adiciona pontos ao produtor e verifica se subiu de nível. - Lógica de gamificação central do sistema.
     */
    public String adicionarPontos(int pontos) {
        this.pontos += pontos;
        int nivelAnterior = this.nivel;
        this.nivel = calcularNivel();

        if (this.nivel > nivelAnterior) {
            return "Parabéns! Você subiu para o nível " + getNomeNivel();
        }
        return "+" + pontos + " pontos! Total: " + this.pontos;
    }

    /**
     * Calcula o nível baseado nos pontos
     */
    private int calcularNivel() {
        if (pontos >= 5000) {
            return 5; // Mestre da Terra
        } else if (pontos >= 2000) {
            return 4; // Guardião da Colheita
        } else if (pontos >= 800) {
            return 3; // Cultivador
        } else if (pontos >= 200) {
            return 2; // Lavrador
        } else {
            return 1; // Semeador
        }
    }

    public String getNomeNivel() {
        return switch (this.nivel) {
            case 1 -> "🌱 Semeador";
            case 2 -> "🌿 Lavrador";
            case 3 -> "🌾 Cultivador";
            case 4 -> "🏆 Guardião da Colheita";
            case 5 -> "👑 Mestre da Terra";
            default -> "Desconhecido";
        };
    }


    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public int getPontos() { return pontos; }

    public int getNivel() { return nivel; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public List<Fazenda> getFazendas() { return fazendas; }
    public void setFazendas(List<Fazenda> fazendas) { this.fazendas = fazendas; }
}
