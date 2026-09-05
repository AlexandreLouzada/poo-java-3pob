package br.edu.universidade.sistema.concorrencia;

import br.edu.universidade.sistema.concorrencia.dominio.SolicitacaoPedido;
import br.edu.universidade.sistema.concorrencia.service.CheckoutAssincronoService;
import java.util.concurrent.CompletableFuture;

// 3. Aplicação Executável demonstrando a composição assíncrona
public class ConcorrenciaApp {
    public static void main(String[] args) {
        System.out.println("========== MOTOR DE CONCORRÊNCIA CORPORATIVO ==========");
        System.out.printf("Thread Principal: %s%n%n", Thread.currentThread().getName());

        CheckoutAssincronoService checkoutService = new CheckoutAssincronoService(4);

        SolicitacaoPedido p1 = new SolicitacaoPedido(101L, "Beatriz Costa", 850.00);
        SolicitacaoPedido p2 = new SolicitacaoPedido(102L, "Carlos Eduardo", 7500.00); // Alto valor

        long inicio = System.currentTimeMillis();

        // Disparo assíncrono: a thread main continua livre imediatamente!
        CompletableFuture<String> resultadoP1 = checkoutService.processarCheckoutCompleto(p1);
        CompletableFuture<String> resultadoP2 = checkoutService.processarCheckoutCompleto(p2);

        System.out.println("[Main] Os dois pedidos foram submetidos de forma concorrente.");
        System.out.println("[Main] A aplicação está livre para processar outras rotinas...\n");

        // join() é utilizado aqui apenas no ponto final de entrega para aguardar o término
        CompletableFuture.allOf(resultadoP1, resultadoP2).join();

        long duracaoTotal = System.currentTimeMillis() - inicio;

        System.out.println("\n--- RESULTADOS FINAIS DA LIQUIDAÇÃO ---");
        System.out.println("Resultado Pedido 101: " + resultadoP1.join());
        System.out.println("Resultado Pedido 102: " + resultadoP2.join());
        System.out.printf("Tempo Total Gasto     : %d ms (Execução paralela confirmada)%n", duracaoTotal);

        checkoutService.encerrar();
        System.out.println("========================================================");
    }
}
