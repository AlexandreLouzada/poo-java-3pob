import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class Exercicio5CombinacaoFutures {
    private static void simularAtraso(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        System.out.println("Iniciando cotação de pacote de viagem...");

        // Serviço 1: Passagem aérea em background
        CompletableFuture<Double> cotacaoPassagem = CompletableFuture.supplyAsync(() -> {
            System.out.println("Consultando companhias aéreas...");
            simularAtraso(1200);
            return 1850.00;
        }).exceptionally(ex -> {
            System.err.println("Falha ao cotar passagem: " + ex.getMessage());
            return 0.0;
        });

        // Serviço 2: Hotel em background
        CompletableFuture<Double> cotacaoHospedagem = CompletableFuture.supplyAsync(() -> {
            System.out.println("Consultando rede hoteleira...");
            simularAtraso(800);
            return 1200.00;
        }).exceptionally(ex -> {
            System.err.println("Falha ao cotar hospedagem: " + ex.getMessage());
            return 0.0;
        });

        // Combinação dos dois futuros independentes
        CompletableFuture<String> pacoteConsolidado = cotacaoPassagem.thenCombine(
                cotacaoHospedagem,
                (precoVoo, precoHotel) -> {
                    double total = precoVoo + precoHotel;
                    return String.format("Voo: R$ %.2f | Hotel: R$ %.2f | Total do Pacote: R$ %.2f",
                            precoVoo, precoHotel, total);
                }
        );

        System.out.println("Aguardando consolidação dos serviços...");
        System.out.println("\n>>> Resumo da Viagem: " + pacoteConsolidado.join());
    }
}
