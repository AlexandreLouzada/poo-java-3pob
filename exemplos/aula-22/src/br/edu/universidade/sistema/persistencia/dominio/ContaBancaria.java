package br.edu.universidade.sistema.persistencia.dominio;

// 1. Entidade de Domínio
public class ContaBancaria {
    private Long id;
    private final String numeroConta;
    private final String titular;
    private double saldo;

    public ContaBancaria(Long id, String numeroConta, String titular, double saldo) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.saldo = saldo;
    }

    public ContaBancaria(String numeroConta, String titular, double saldoInicial) {
        this(null, numeroConta, titular, saldoInicial);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroConta() { return numeroConta; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    public void debitar(double valor) {
        if (valor <= 0 || valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente ou valor de débito inválido.");
        }
        this.saldo -= valor;
    }

    public void creditar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de crédito inválido.");
        }
        this.saldo += valor;
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] Conta: %-8s | Titular: %-15s | Saldo: R$ %8.2f",
                id, numeroConta, titular, saldo);
    }
}
