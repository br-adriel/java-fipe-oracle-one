package br.com.alura.fipe.services;

import br.com.alura.fipe.models.DadosMarca;
import br.com.alura.fipe.models.TipoVeiculo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

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
}
