package br.edu.universidade.sistema.tarifas;

public abstract class Conta {
    protected String numeroConta;
    protected double saldo;

    public Conta(String numeroConta, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.saldo = (saldoInicial >= 0.0) ? saldoInicial : 0.0;
    }

    public void depositar(double valor) {
        if (valor > 0.0) {
            this.saldo += valor;
        }
    }

    public abstract boolean sacar(double valor);

    public double getSaldo() {
        return saldo;
    }

    public void exibirDados() {
        System.out.printf("Conta %s | Saldo: R$ %.2f%n", numeroConta, saldo);
    }
}