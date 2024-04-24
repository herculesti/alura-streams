package br.com.alura.tabelafipe.principal;

import br.com.alura.tabelafipe.model.DadosRecord;
import br.com.alura.tabelafipe.model.ModeloRecord;
import br.com.alura.tabelafipe.model.VeiculoRecord;
import br.com.alura.tabelafipe.service.ConsumoApi;
import br.com.alura.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO_MARCA = "https://parallelum.com.br/fipe/api/v1/%s/marcas";
    private final String ENDERECO_MODELO = ENDERECO_MARCA + "/%s/modelos";
    private final String ENDERECO_ANO = ENDERECO_MODELO + "/%s/anos";

    public void exibeMenu() {

        var jsonRetorno = "";

        System.out.println("""
                
                Tipos: carros; caminhoes; motos
                Digite o tipo de veículo desejado:
                """);
        var tipoVeiculo = leitura.nextLine();
        jsonRetorno = consumo.obterDados(String.format(ENDERECO_MARCA, tipoVeiculo));
        var marcas = conversor.obterDadosLista(jsonRetorno, DadosRecord.class);
        marcas.stream()
                .sorted(Comparator.comparing(DadosRecord::nome))
                .forEach(System.out::println);

        System.out.println("\nEscolha um código de uma das marcas acima:");
        var codigoMarca = leitura.nextLine();
        jsonRetorno = consumo.obterDados(String.format(ENDERECO_MODELO, tipoVeiculo, codigoMarca));
        var modelos = conversor.obterDadosObjeto(jsonRetorno, ModeloRecord.class);
        modelos.modelos()
                .stream()
                .sorted(Comparator.comparing((DadosRecord::nome)))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do modelo que deseja visualizar, por exemplo PALIO.");
        var nomeVeiculo = leitura.nextLine();
        var modelosFiltrados = modelos.modelos()
                .stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação::");
        var modeloVeiculo = leitura.nextLine();
        jsonRetorno = consumo.obterDados(String.format(ENDERECO_ANO, tipoVeiculo, codigoMarca, modeloVeiculo));
        var anos = conversor.obterDadosLista(jsonRetorno, DadosRecord.class);

        var veiculos = new ArrayList<VeiculoRecord>();
        var url = String.format(ENDERECO_ANO, tipoVeiculo, codigoMarca, modeloVeiculo);
        for (DadosRecord ano : anos) {
            jsonRetorno = consumo.obterDados(url + "/" + ano.codigo());
            VeiculoRecord veiculo = conversor.obterDadosObjeto(jsonRetorno, VeiculoRecord.class);
            veiculos.add(veiculo);
        }
        System.out.println("\nTodos veículos filtrados com avaliações por ano:");
        veiculos.forEach(System.out::println);
        
    }
}
