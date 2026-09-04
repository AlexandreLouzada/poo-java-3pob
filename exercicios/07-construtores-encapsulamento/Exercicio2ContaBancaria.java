import java.util.Locale;

public class Exercicio2ContaBancaria {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        ContaBancaria conta = new ContaBancaria("9876-1", "Carlos Andrade", 500.0);

        System.out.printf("Conta: %s | Titular: %s | Saldo: R$ %.2f%n", conta.getNumeroConta(), conta.getTitular(), conta.getSaldo());

        conta.depositar(250.0);
        conta.sacar(1000.0);
        conta.sacar(300.0);

        System.out.printf("Saldo final: R$ %.2f%n", conta.getSaldo());
    }
}
