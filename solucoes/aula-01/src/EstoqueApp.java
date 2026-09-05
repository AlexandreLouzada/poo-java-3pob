public class EstoqueApp {
    public static void main(String[] args) {
        ItemEstoque teclado = new ItemEstoque("TEC-001", "Teclado USB", 10, 85.50);
        ItemEstoque monitor = new ItemEstoque("MON-002", "Monitor 27 Pol", -5, 1400.00);

        System.out.println("--- Fichas Iniciais ---");
        teclado.exibirFicha();
        monitor.exibirFicha();

        System.out.println("\n--- Movimentacao 1: Entrada legítima de 20 teclados ---");
        boolean entrou = teclado.adicionarEstoque(20);
        System.out.printf("Entrada autorizada? %s | Nova quantidade: %d%n", entrou, teclado.getQuantidade());

        System.out.println("\n--- Movimentacao 2: Saída de 25 teclados (saldo suficiente) ---");
        boolean saiu = teclado.removerEstoque(25);
        System.out.printf("Saída autorizada? %s | Nova quantidade: %d%n", saiu, teclado.getQuantidade());

        System.out.println("\n--- Tentativa de retirada excessiva: 27 teclados ---");
        boolean excesso = teclado.removerEstoque(27);
        System.out.printf("Saída autorizada? %s | Quantidade preservada: %d (invariante protegida)%n",
                excesso, teclado.getQuantidade());

        System.out.println("\n--- Tentativa de entrada inválida (quantidade <= 0) ---");
        System.out.printf("Entrada de -3 autorizada? %s | Quantidade preservada: %d%n",
                teclado.adicionarEstoque(-3), teclado.getQuantidade());

        System.out.println("\n--- Tenta violar o estoque negativo na inicializacao ---");
        System.out.println("Item 'monitor' iniciado com quantidade negativa foi corrigida para: "
                + monitor.getQuantidade() + " (guarda do construtor)");

        System.out.println("\n--- Fichas Finais ---");
        teclado.exibirFicha();
        monitor.exibirFicha();
    }
}

// Desafio CLI (exercicio da aula):
// 1. javac ItemEstoque.java EstoqueApp.java   -> compila as duas classes
// 2. java EstoqueApp                           -> executa a aplicacao
// 3. javap -c ItemEstoque                      -> inspeciona o bytecode gerado