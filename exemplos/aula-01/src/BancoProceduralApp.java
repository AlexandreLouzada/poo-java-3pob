public class BancoProceduralApp {
    public static void main(String[] args) {
        // Dados soltos na Stack sem proteção contra mutação indevida
        double saldo = 100.0;

        // Função utilitária externa aplicando saque legítimo
        saldo = sacar(saldo, 40.0);
        System.out.printf("Saldo após saque legítimo: R$ %.2f%n", saldo);

        // Ponto de Ruptura: qualquer ponto do sistema pode quebrar a invariante
        saldo = -50000.0;
        System.out.printf("ALERTA: Saldo violado arbitrariamente: R$ %.2f%n", saldo);
    }

    public static double sacar(double saldoAtual, double valor) {
        if (valor > 0 && saldoAtual >= valor) {
            return saldoAtual - valor;
        }
        System.err.println("Erro: Saldo insuficiente ou valor inválido.");
        return saldoAtual;
    }
}
