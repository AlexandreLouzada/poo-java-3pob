import java.util.Locale;

class ContaPoupanca extends Conta {
    private double taxaRendimento;

    public ContaPoupanca(String numero, double saldoInicial, double taxaRendimento) {
        super(numero, saldoInicial);
        this.taxaRendimento = taxaRendimento;
    }

    public void aplicarRendimento() {
        double rendimento = getSaldo() * (taxaRendimento / 100.0);
        depositar(rendimento);
        System.out.printf("Rendimento de R$ %.2f aplicado à Conta Poupança.%n", rendimento);
    }
}
