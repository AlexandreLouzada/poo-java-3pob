import java.util.Locale;

class ContaCorrente {
    private final String numero;
    private double saldo;

    public ContaCorrente(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = Math.max(0, saldoInicial);
    }

    public double getSaldo() {
        return saldo;
    }

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente para efetuar o saque.", 
                this.saldo, 
                valor
            );
        }
        this.saldo -= valor;
        System.out.printf("Saque de R$ %.2f realizado na conta %s. Saldo restante: R$ %.2f%n", 
                          valor, numero, this.saldo);
    }
}
