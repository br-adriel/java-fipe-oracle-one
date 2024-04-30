package br.com.alura.fipe.models;

public enum TipoVeiculo {
    CAMINHAO("caminhoes"),
    CARRO("carros"),
    MOTO("motos");

    private final String slug;

    TipoVeiculo(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }
}
