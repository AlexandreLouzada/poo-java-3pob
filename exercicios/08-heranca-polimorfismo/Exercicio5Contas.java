import java.util.Locale;

public class Exercicio5Contas {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        ContaPoupanca cp = new ContaPoupanca("CP-101", 1000.0, 0.5);
        ContaCorrente cc = new ContaCorrente("CC-202", 500.0, 300.0);

        System.out.println("--- Operações na Conta Poupança ---");
        cp.sacar(200.0);
        cp.aplicarRendimento();
        System.out.printf("Saldo CP: R$ %.2f%n", cp.getSaldo());

        System.out.println("\n--- Operações na Conta Corrente ---");
        cc.sacar(600.0); // Usa 100.0 do cheque especial + 2.0 de taxa
        System.out.printf("Saldo CC: R$ %.2f%n", cc.getSaldo());
    }
}
