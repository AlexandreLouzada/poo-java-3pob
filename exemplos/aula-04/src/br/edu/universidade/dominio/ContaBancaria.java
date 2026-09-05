package br.edu.universidade.dominio;

public class ContaBancaria {
    // 1. Atributos de Instância (Estado do Objeto)
    String numero;
    String titular;
    double saldo;

    // 2. Construtor com Sobrecarga e Encadeamento
    public ContaBancaria(String numero, String titular) {
        this(numero, titular, 0.0); // Delega para o construtor completo
    }

    public ContaBancaria(String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        // Aplicação de regra de guarda na inicialização
        if (saldoInicial >= 0.0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }
    }

    // 3. Métodos (Comportamento e Regras de Guarda)
    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }

    public void exibirExtrato() {
        System.out.printf("Conta: %-8s | Titular: %-15s | Saldo: R$ %8.2f%n",
                this.numero, this.titular, this.saldo);
    }
}
