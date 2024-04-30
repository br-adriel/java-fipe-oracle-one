package br.com.alura.fipe.clis;

import br.com.alura.fipe.models.DadosMarca;
import br.com.alura.fipe.models.DadosModelo;
import br.com.alura.fipe.models.TipoVeiculo;
import br.com.alura.fipe.services.FipeApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainCli {
    private final Scanner sc = new Scanner(System.in);
    private final Map<TipoVeiculo, ArrayList<DadosMarca>> marcas = new HashMap<>();
    private final Map<String, ArrayList<DadosModelo>> modelos = new HashMap<>();
    private final FipeApiService fipeApiService = new FipeApiService();

    public void fluxoPrincipal() {
        System.out.println("Bem vindo(a) ao consultor de veículos");
        var tipoVeiculo = mostrarMenuVeiculos();

        carregarMarcas(tipoVeiculo);
        var marca = mostrarMenuMarcas(tipoVeiculo);

        carregarModelos(tipoVeiculo, marca);
        var chaveModelos = gerarChaveParaModelo(tipoVeiculo, marca);
        modelos.get(chaveModelos).forEach(System.out::println);

        menuBuscarModelo(chaveModelos);
        DadosModelo modeloEscolhido = menuSelecaoModelo(chaveModelos);
        exibirTabelaFipe(tipoVeiculo, marca, modeloEscolhido);
    }

    private TipoVeiculo mostrarMenuVeiculos() {
        while (true) {
            System.out.println("---------------------------------------------");
            System.out.println("Escolha o tipo de veículo:");
            System.out.println("1) Caminhão");
            System.out.println("2) Carro");
            System.out.println("3) Moto");
            System.out.print(">> ");
            int opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    return TipoVeiculo.CAMINHAO;
                case 2:
                    return TipoVeiculo.CARRO;
                case 3:
                    return TipoVeiculo.MOTO;
                default:
                    System.out.println("\n[!] - Opção inválida\n");
            }
        }
    }

    private void carregarMarcas(TipoVeiculo tipoVeiculo) {
        System.out.println("\nCarregando opções de marca...\n");
        if (marcas.get(tipoVeiculo) == null) {
            marcas.put(tipoVeiculo, fipeApiService.getMarcas(tipoVeiculo));
        }
    }

    private DadosMarca mostrarMenuMarcas(TipoVeiculo tipoVeiculo) {
        var marcasTipoVeiculo = marcas.get(tipoVeiculo);

        while (true) {
            System.out.println("---------------------------------------------");
            System.out.println("Digite o código da marca desejada:");

            marcasTipoVeiculo
                    .forEach(m -> System.out.println(m.nome() + " - [" + m.codigo() + "]"));
            System.out.print(">> ");
            String codigo = sc.nextLine();

            var marca = marcasTipoVeiculo
                    .stream()
                    .filter(m -> m.codigo().equals(codigo))
                    .findFirst();
            if (marca.isPresent()) return marca.get();
            else System.out.println("\n[!] - Código inválido\n");
        }
    }

    private void carregarModelos(TipoVeiculo tipoVeiculo, DadosMarca marca) {
        System.out.println("\nCarregando modelos...\n");
        var chave = gerarChaveParaModelo(tipoVeiculo, marca);
        if (!modelos.containsKey(chave)) {
            var modelosRecuperados = fipeApiService.getModelos(tipoVeiculo, marca);
            modelos.put(chave, modelosRecuperados);
        }
    }

    private String gerarChaveParaModelo(TipoVeiculo tipoVeiculo, DadosMarca marca) {
        return tipoVeiculo.getSlug() + "-" + marca.codigo();
    }

    private ArrayList<DadosModelo> buscarModelo(String chave, String busca) {
        return modelos
                .get(chave)
                .stream()
                .filter(m -> m.nome().toLowerCase().contains(busca.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void menuBuscarModelo(String chave) {
        System.out.println("---------------------------------------------");
        System.out.println("Busque o modelo desejado:");
        System.out.print(">> ");
        String busca = sc.nextLine();

        System.out.println("\nPesquisando...\n");

        var resultado = buscarModelo(chave, busca);
        System.out.println("Resultado da busca:");
        resultado.forEach(r -> System.out.println(r.nome() + " - [" + r.codigo() + "]"));
    }

    private DadosModelo menuSelecaoModelo(String chave) {
        var subListaModelos = modelos.get(chave);
        while (true) {
            System.out.println("---------------------------------------------");
            System.out.println("Digite o código do modelo desejado:");
            String codigo = sc.nextLine();

            var modeloEscolhido = subListaModelos.stream()
                    .filter(m -> m.codigo().equals(codigo)).findFirst();
            if (modeloEscolhido.isPresent()) {
                return modeloEscolhido.get();
            }
            System.out.println("\n[!] - Código inválido\n");
        }
    }

    private void exibirTabelaFipe(
            TipoVeiculo tipoVeiculo,
            DadosMarca marca,
            DadosModelo modelo
    ) {
        System.out.println("\nCarregando informações...\n");
        var informacoesVeiculos = fipeApiService.getInformacoesVeiculos(tipoVeiculo, marca, modelo);
        System.out.println("---------------------------------------------");
        informacoesVeiculos.forEach(iv -> System.out.printf(
                "%s (%s, %s) - %s\n",
                iv.modelo(), iv.anoModelo(), iv.combustivel(), iv.valor()
        ));
    }
}
