package br.edu.universidade.sistema.tarifas;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(String numeroConta, double saldoInicial) {
        super(numeroConta, saldoInicial);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }
}