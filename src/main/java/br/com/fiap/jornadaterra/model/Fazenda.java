package br.com.fiap.jornadaterra.model;

import br.com.fiap.jornadaterra.enums.TipoCultura;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a fazenda do produtor.
 * Demonstra: Herança (Fazenda é composta de Setores), Encapsulamento, Construtores.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fazendas")
public class Fazenda {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da fazenda é obrigatório")
    private String nome;


    // Área total em hectares
    @Positive(message = "Área deve ser positiva")
    private Double areaHectares;

    @Embedded
    private Localizacao localizacao;


    @Enumerated(EnumType.STRING)
    private TipoCultura TipoCultura;

    // Relacionamento com produtor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produtor_id", nullable = false)
    @JsonBackReference
    private Produtor produtor;

    // Uma fazenda possui vários setores geográficos
    @OneToMany(mappedBy = "fazenda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Setor> setores = new ArrayList<>();


    public Fazenda(String nome, Double latitude, Double longitude,
                   Double areaHectares, String municipio, String estado,
                   TipoCultura tipoCultura, Produtor produtor) {
        this.nome = nome;
        this.localizacao = new Localizacao(latitude, longitude, municipio, estado);
        this.areaHectares = areaHectares;
        this.TipoCultura = tipoCultura;
        this.produtor = produtor;
    }



    /**
     * Retorna o setor com maior risco climático no momento
     */
    public Setor getSetorMaiorRisco() {
        Setor setorCritico = null;
        int maiorRisco = 0;

        for (Setor setor : setores) {
            if (setor.getNivelRisco() > maiorRisco) {
                maiorRisco = setor.getNivelRisco();
                setorCritico = setor;
            }
        }
        return setorCritico;
    }

    /**
     * Calcula percentual de área em risco
     */
    public Double getPercentualEmRisco() {
        if (setores.isEmpty()) return 0.00;

        long setoresEmRisco = setores.stream()
                .filter(s -> s.getNivelRisco() >= 3)
                .count();

        return (double) setoresEmRisco / setores.size() * 100;
    }

}
