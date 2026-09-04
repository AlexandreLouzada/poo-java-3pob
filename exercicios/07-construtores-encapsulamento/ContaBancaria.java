import java.util.Locale;

class ContaBancaria {
    private final String numeroConta;
    private String titular;
    private double saldo;

    public ContaBancaria(String numeroConta, String titular) {
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.saldo = 0.0;
    }

    public ContaBancaria(String numeroConta, String titular, double depositoInicial) {
        this.numeroConta = numeroConta;
        this.titular = titular;
        if (depositoInicial > 0) {
            this.saldo = depositoInicial;
        }
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            System.out.printf("Depósito de R$ %.2f efetuado na conta %s.%n", valor, numeroConta);
        } else {
            System.out.println("Erro: Valor de depósito inválido.");
        }
    }

    public void sacar(double valor) {
        if (valor > 0 && saldo >= valor) {
            saldo -= valor;
            System.out.printf("Saque de R$ %.2f efetuado na conta %s.%n", valor, numeroConta);
        } else {
            System.out.printf("Erro: Saldo insuficiente ou valor inválido para saque na conta %s.%n", numeroConta);
        }
    }
}
