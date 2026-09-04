import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class Exercicio4CompletableFuturePipeline {
    private static void simularAtraso(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        System.out.println("[Main] Iniciando checkout de pedidos...");

        CompletableFuture<Void> pipeline = CompletableFuture
                // 1. Busca assíncrona dos dados do pedido
                .supplyAsync(() -> {
                    System.out.println("[Async] Buscando pedido no banco de dados...");
                    simularAtraso(1000);
                    return new Pedido("PED-2026-X", 450.0);
                })
                // 2. Cálculo e acréscimo de frete
                .thenApply(pedido -> {
                    System.out.println("[Async] Calculando frete e impostos...");
                    simularAtraso(500);
                    double frete = 35.0;
                    return pedido.getSubtotal() + frete;
                })
                // 3. Consumidor final: notificação
                .thenAccept(totalFinal -> {
                    System.out.printf("[Async] Pedido confirmado! Total final faturado: R$ %.2f%n", totalFinal);
                });

        System.out.println("[Main] Thread principal livre para receber outras requisições.");

        // Aguarda a conclusão apenas para permitir a visualização no terminal de teste
        pipeline.join();
    }
}
