package br.edu.universidade.sistema.concorrencia.service;

import br.edu.universidade.sistema.concorrencia.dominio.SolicitacaoPedido;
import java.util.concurrent.*;

// 2. Serviço de Checkout Assíncrono e Não-Bloqueante
public class CheckoutAssincronoService {

    private final ExecutorService threadPool;

    public CheckoutAssincronoService(int totalWorkers) {
        // Pool de threads dedicado para isolamento de carga corporativa
        this.threadPool = Executors.newFixedThreadPool(totalWorkers);
    }

    // Tarefa Assíncrona 1: Consulta de Estoque em microsserviço externo
    public CompletableFuture<Boolean> verificarEstoqueAsync(SolicitacaoPedido pedido) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatenciaRede(300); // 300 ms de latência simulada
            System.out.printf("[%s] Estoque validado com sucesso para Pedido #%d%n",
                    Thread.currentThread().getName(), pedido.getIdPedido());
            return true;
        }, threadPool);
    }

    // Tarefa Assíncrona 2: Motor Antifraude de Risco e Score
    public CompletableFuture<Integer> calcularScoreFraudeAsync(SolicitacaoPedido pedido) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatenciaRede(500); // 500 ms de latência simulada
            int score = (pedido.getValorTotal() > 5000.0) ? 85 : 20; // Risco de 0 a 100
            System.out.printf("[%s] Antifraude concluído. Score apurado: %d%n",
                    Thread.currentThread().getName(), score);
            return score;
        }, threadPool);
    }

    // Tarefa Assíncrona 3: Orquestração e Autorização de Pagamento
    public CompletableFuture<String> processarCheckoutCompleto(SolicitacaoPedido pedido) {
        CompletableFuture<Boolean> tarefaEstoque = verificarEstoqueAsync(pedido);
        CompletableFuture<Integer> tarefaAntifraude = calcularScoreFraudeAsync(pedido);

        // thenCombine: Dispara ambas em paralelo e junta os resultados sem travar threads
        return tarefaEstoque.thenCombineAsync(tarefaAntifraude, (estoqueDisponivel, scoreFraude) -> {
            System.out.printf("[%s] Consolidando decisões para Pedido #%d...%n",
                    Thread.currentThread().getName(), pedido.getIdPedido());

            if (!estoqueDisponivel) {
                return "RECUSADO: Produto esgotado nos armazéns.";
            }
            if (scoreFraude > 70) {
                return "RECUSADO: Reprovado pela mesa de análise de risco e antifraude.";
            }

            return String.format("APROVADO: Pedido #%d faturado e autorizado via Cartão!", pedido.getIdPedido());
        }, threadPool);
    }

    public void encerrar() {
        // Encerramento gracioso do pool liberando descritores do SO
        this.threadPool.shutdown();
        try {
            if (!this.threadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                this.threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void simularLatenciaRede(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
