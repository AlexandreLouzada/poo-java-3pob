package br.edu.universidade.sistema.cambio;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AgregadorCambioService {
    private final ExecutorService executor;

    public AgregadorCambioService(int numeroThreads) {
        this.executor = Executors.newFixedThreadPool(numeroThreads);
    }

    public CompletableFuture<CotacaoMoeda> consultarProvedorAsync(CotacaoMoeda cotacao) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(cotacao.getTempoRespostaMs());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Consulta interrompida para o provedor: " + cotacao.getProvedor());
            }
            return cotacao;
        }, executor);
    }

    public CotacaoMoeda buscarMelhorCotacao(List<CotacaoMoeda> candidatas) {
        CompletableFuture<?>[] tarefas = candidatas.stream()
                .map(this::consultarProvedorAsync)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(tarefas).join();

        return candidatas.stream()
                .min((a, b) -> Double.compare(a.getValorTaxa(), b.getValorTaxa()))
                .orElseThrow(() -> new IllegalStateException("Nenhuma cotacao disponivel."));
    }

    public long calcularTempoTotal(List<CotacaoMoeda> candidatas) {
        return candidatas.stream()
                .map(CotacaoMoeda::getTempoRespostaMs)
                .reduce(0L, Long::sum);
    }

    public void encerrar() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}