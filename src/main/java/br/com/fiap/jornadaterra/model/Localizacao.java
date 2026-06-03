package br.com.fiap.jornadaterra.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Localizacao {

    private Double latitude;
    private Double longitude;
    private String municipio;
    private String estado;

    public Localizacao() {}

    public Localizacao(Double latitude, Double longitude, String municipio, String estado) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.municipio = municipio;
        this.estado = estado;
    }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}