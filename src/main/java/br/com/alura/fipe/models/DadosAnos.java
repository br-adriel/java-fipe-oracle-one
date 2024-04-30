package br.com.alura.fipe.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosAnos(
        @JsonAlias("codigo") String codigo,
        @JsonAlias("nome") String nome
) {
}
