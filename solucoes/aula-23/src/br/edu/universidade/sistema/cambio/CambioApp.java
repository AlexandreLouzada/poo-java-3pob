package br.edu.universidade.sistema.cambio;

import java.util.List;

public class CambioApp {
    public static void main(String[] args) {
        System.out.println("--- Executor com 3 Threads consultando 3 provedores em paralelo ---");
        AgregadorCambioService agregador = new AgregadorCambioService(3);

        List<CotacaoMoeda> cotacoes = List.of(
                new CotacaoMoeda("Banco Alpha", "USD", 5.45, 400),
                new CotacaoMoeda("Banco Beta", "USD", 5.41, 700),
                new CotacaoMoeda("Banco Gamma", "USD", 5.48, 200)
        );

        long inicio = System.currentTimeMillis();
        CotacaoMoeda melhor = agregador.buscarMelhorCotacao(cotacoes);
        long fim = System.currentTimeMillis();

        System.out.println("\n--- Cotacoes consultadas com supplyAsync ---");
        cotacoes.forEach(System.out::println);

        System.out.println("\n--- Melhor cotacao (menor taxa) ---");
        System.out.println(melhor);

        long tempoParalelo = fim - inicio;
        System.out.printf("%nTempo total na execucao PARALELA: %d ms (limite do maior tempo: ~%d ms)%n",
                tempoParalelo, melhor.getTempoRespostaMs());
        System.out.printf("Soma dos tempos individuais (execucao sequencial): %d ms%n",
                agregador.calcularTempoTotal(cotacoes));

        if (melhor.getProvedor().equals("Banco Beta")) {
            System.out.println("\nResultado esperado pelo desafio: Banco Beta venceu (taxa 5.4100). OK!");
        }

        agregador.encerrar();
    }
}