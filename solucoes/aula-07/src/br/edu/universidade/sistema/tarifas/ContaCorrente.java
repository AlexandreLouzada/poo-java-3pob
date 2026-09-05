package br.edu.universidade.sistema.tarifas;

public class ContaCorrente extends Conta implements Tributavel {

    private static final double TAXA_SAQUE = 1.50;

    public ContaCorrente(String numeroConta, double saldoInicial) {
        super(numeroConta, saldoInicial);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= (valor + TAXA_SAQUE)) {
            this.saldo -= (valor + TAXA_SAQUE);
            return true;
        }
        return false;
    }

    @Override
    public double calcularTributo() {
        return this.saldo * 0.01;
    }
}