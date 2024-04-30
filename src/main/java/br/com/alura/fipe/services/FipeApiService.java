package br.com.alura.fipe.services;

import br.com.alura.fipe.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class FipeApiService extends ConsumoApi {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String BASE_URL = "https://parallelum.com.br/fipe/api/v1/";

    public ArrayList<DadosMarca> getMarcas(TipoVeiculo tipoVeiculo) {
        var json = super.obterDados(BASE_URL + tipoVeiculo.getSlug() + "/marcas");
        try {
            return mapper.readValue(
                    json, new TypeReference<ArrayList<DadosMarca>>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<DadosModelo> getModelos(TipoVeiculo tipoVeiculo, DadosMarca marca) {
        var json = super.obterDados(
                BASE_URL + tipoVeiculo.getSlug() + "/marcas/"
                + marca.codigo() + "/modelos"
        );
        try {
            var parsedJson = mapper.readValue(
                    json,
                    new TypeReference<Map<String, ArrayList<DadosModelo>>>() {
                    }
            );
            return parsedJson.get("modelos");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<DadosVeiculo> getInformacoesVeiculos(
            TipoVeiculo tipoVeiculo,
            DadosMarca marca,
            DadosModelo modelo
    ) {
        var url = BASE_URL + tipoVeiculo.getSlug() + "/marcas/"
                  + marca.codigo() + "/modelos/" + modelo.codigo() + "/anos";
        var json = super.obterDados(url);
        try {
            var anos = mapper.readValue(
                    json,
                    new TypeReference<ArrayList<DadosAnos>>() {
                    });
            return anos.stream()
                    .map(a -> getDadosVeiculo(url + "/" + a.codigo()))
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private DadosVeiculo getDadosVeiculo(String url) {
        var json = super.obterDados(url);
        try {
            return mapper.readValue(json, DadosVeiculo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
