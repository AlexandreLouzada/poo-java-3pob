package br.edu.universidade.financeiro;

/**
 * Executável de demonstração do uso da classe utilitária CalculadoraFinanceira.
 */
public final class CalculadoraFinanceiraApp {
    public static void main(String[] args) {
        System.out.println("--- Teste 1: Juros Compostos (Capital R$ 1.000,00 | 2% a.m. | 12 meses) ---");
        double montante = CalculadoraFinanceira.calcularJurosCompostos(1000.00, 0.02, 12);
        System.out.printf("Montante acumulado: R$ %.2f%n", montante);

        System.out.println("\n--- Teste 2: Cota de Amortizacao Constante (SAC | Saldo R$ 60.000,00 | 24 meses) ---");
        double cota = CalculadoraFinanceira.calcularAmortizacaoConstante(60000.00, 24);
        System.out.printf("Cota fixa de amortizacao: R$ %.2f%n", cota);

        System.out.println("\n--- Teste 3: Validacao de precondicoes (expectativa: IllegalArgumentException) ---");
        try {
            CalculadoraFinanceira.calcularJurosCompostos(-100.00, 0.02, 12);
            System.out.println("ERRO: deveria ter lançado exceção para capital negativo.");
        } catch (IllegalArgumentException ex) {
            System.out.println("[OK] Exceção capturada: " + ex.getMessage());
        }

        try {
            CalculadoraFinanceira.calcularJurosCompostos(1000.00, 1.5, 12);
            System.out.println("ERRO: deveria ter lançado exceção para taxa acima de 1.0.");
        } catch (IllegalArgumentException ex) {
            System.out.println("[OK] Exceção capturada: " + ex.getMessage());
        }

        try {
            CalculadoraFinanceira.calcularAmortizacaoConstante(60000.00, 0);
            System.out.println("ERRO: deveria ter lançado exceção para prazo zerado.");
        } catch (IllegalArgumentException ex) {
            System.out.println("[OK] Exceção capturada: " + ex.getMessage());
        }
    }

    private CalculadoraFinanceiraApp() {}
}