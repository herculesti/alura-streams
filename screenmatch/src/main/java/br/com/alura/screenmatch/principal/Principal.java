package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ac7d80c7";

    public void exibeMenu() {
        System.out.println("Digite o nome da série:");
        var nomeSerie = leitura.nextLine();
        var url = ENDERECO + nomeSerie.replace(" ", "+") + API_KEY;
        var json = consumo.obterDados(url);
        var dados = conversor.obterDados(json, DadosSeries.class);
        System.out.println(dados);

        var temporadas = new ArrayList<DadosTemporada>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {

            url = ENDERECO + nomeSerie.replace(" ", "+") + API_KEY + "&season=" + i;
            json = consumo.obterDados(url);
            var dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        //utilizando repeticao tradicional
        for (int i = 0; i < dados.totalTemporadas(); i++) {
            var episodios = temporadas.get(i).episodios();
            for (int j = 0; j < episodios.size(); j++) {
                System.out.println(episodios.get(j).titulo());
            }
        }

        //utilizando lambda
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        //utilizando streams

//        //exemplo simples com nomes
//        var nomes = Arrays.asList("Deca", "Nego", "Agatha", "Icaro", "Iva");
//        nomes.stream()
//                .sorted()
//                //.limit(3)
//                .filter(n -> n.startsWith("I"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);



        var dadosEpisodios =  temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                //.toList() //parecido com o collect, porém gera uma lista imutável
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios");
//        dadosEpisodios
//                .stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                //.peek(e -> System.out.println("primeiro filtro (N/A)" + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                //.peek(e -> System.out.println("ordenacao" + e))
//                .limit(10)
//                //.peek(e -> System.out.println("limite (10)" + e))
//                .map(e -> e.titulo().toUpperCase())
//                //.peek(e -> System.out.println("uppercase" + e))
//                .forEach(System.out::println);

        var episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);



//        System.out.println("Qual o trecho do título que você deseja procurar?");
//        var trechoTitulo = leitura.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
//                .findFirst();
//
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episodio encontrado");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada() +
//                    "\nTítulo: " + episodioBuscado.get().getTitulo());
//        } else {
//            System.out.println("Nenhum episodio encontrado");
//        }


//
//        System.out.println("A partir de que ano você deseja vê os episodios?");
//        var ano = leitura.nextInt();
//        var dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                        "Episódio: " + e.getTitulo() +
//                        "Data lançamento: " + e.getDataLancamento().format(formatador)));


        //Avaliações por temporada
        System.out.println("Avaliação por temporada");
        var avaliacaoPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        //System.out.println(est);
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de avaliações: " + est.getCount());

    }
}
