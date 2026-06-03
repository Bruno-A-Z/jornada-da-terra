package br.com.fiap.jornadaterra.model;

import br.com.fiap.jornadaterra.enums.TipoCultura;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a fazenda do produtor.
 * Demonstra: Herança (Fazenda é composta de Setores), Encapsulamento, Construtores.
 */
@Entity
@Table(name = "fazendas")
public class Fazenda {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da fazenda é obrigatório")
    private String nome;


    // Coordenadas GPS para integração com dados satelitais
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Área total em hectares
    @Positive(message = "Área deve ser positiva")
    private Double areaHectares;

    private String municipio;
    private String estado;


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

    // ===================== CONSTRUTORES =====================-

    public Fazenda() {}

    public Fazenda(String nome, Double latitude, Double longitude,
                   Double areaHectares, String municipio, String estado,
                   TipoCultura TipoCultura, Produtor produtor) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaHectares = areaHectares;
        this.municipio = municipio;
        this.estado = estado;
        this.TipoCultura = TipoCultura;
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


    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getAreaHectares() { return areaHectares; }
    public void setAreaHectares(Double areaHectares) { this.areaHectares = areaHectares; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public TipoCultura getTipoCultura() { return TipoCultura; }
    public void setTipoCultura(TipoCultura tipoCultura) { this.TipoCultura = tipoCultura; }

    public Produtor getProdutor() { return produtor; }
    public void setProdutor(Produtor produtor) { this.produtor = produtor; }

    public List<Setor> getSetores() { return setores; }
    public void setSetores(List<Setor> setores) { this.setores = setores; }
}
