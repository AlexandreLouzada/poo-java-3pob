import java.util.Locale;

class Conta {
    private String numero;
    private double saldo;

    public Conta(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            System.out.printf("Depósito de R$ %.2f efetuado na conta %s.%n", valor, numero);
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0 && saldo >= valor) {
            saldo -= valor;
            System.out.printf("Saque de R$ %.2f efetuado na conta %s.%n", valor, numero);
            return true;
        } else {
            System.out.printf("Saque de R$ %.2f recusado na conta %s: Saldo insuficiente.%n", valor, numero);
            return false;
        }
    }
}
