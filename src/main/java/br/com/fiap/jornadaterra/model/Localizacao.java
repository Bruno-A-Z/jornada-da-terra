package br.com.fiap.jornadaterra.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Localizacao {

    private Double latitude;
    private Double longitude;
    private String municipio;
    private String estado;


}