import java.util.Locale;

public class Exercicio3CheckedException {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        ContaCorrente conta = new ContaCorrente("12345-X", 200.0);

        try {
            conta.sacar(100.0);
            conta.sacar(350.0); // Disparará a exceção
        } catch (SaldoInsuficienteException e) {
            System.out.printf("Erro na operação bancária: %s%n", e.getMessage());
            System.out.printf("Detalhes -> Saldo atual: R$ %.2f | Tentativa de saque: R$ %.2f%n", 
                              e.getSaldoAtual(), e.getValorTentativa());
        }
    }
}
