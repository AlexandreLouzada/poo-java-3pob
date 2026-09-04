
class ContaBancariaTeste {
    private double saldo;

    public ContaBancariaTeste(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public double getSaldo() {
        return saldo;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor de saque deve ser estritamente positivo.");
        }
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo disponível: " + this.saldo);
        }
        this.saldo -= valor;
    }
}
