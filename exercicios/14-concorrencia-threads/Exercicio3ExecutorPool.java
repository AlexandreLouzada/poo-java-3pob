import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Exercicio3ExecutorPool {
    public static void main(String[] args) {
        // Pool limitado a 3 threads simultâneas
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> resultados = new ArrayList<>();

        System.out.println("Enviando 6 tarefas de download para o pool...");
        for (int i = 1; i <= 6; i++) {
            resultados.add(executor.submit(new TarefaDownload(i)));
        }

        // Bloqueio controlado para exibição conforme disponibilidade
        for (Future<String> futuro : resultados) {
            try {
                System.out.println(futuro.get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Erro ao processar tarefa: " + e.getMessage());
            }
        }

        // Encerramento seguro do pool
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
