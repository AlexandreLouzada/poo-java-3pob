import java.util.Locale;

public class Exercicio3ContaBancaria {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        ContaBancaria conta = new ContaBancaria();
        conta.titular = "Maria Silva";
        conta.numeroConta = "12345-6";

        conta.depositar(500.0);
        conta.sacar(200.0);
        conta.sacar(400.0); // Tentativa que ultrapassa o saldo restante

        conta.consultarSaldo();
    }
}
