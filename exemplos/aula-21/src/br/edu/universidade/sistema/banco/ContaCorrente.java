package br.edu.universidade.sistema.banco;

// 1. Classe de Produção sob Teste (Localizada em src/main/java)
public class ContaCorrente {
    private final String numeroConta;
    private double saldo;
    private boolean ativa;
    public static final double TARIFA_SAQUE = 2.50;

    public ContaCorrente(String numeroConta, double saldoInicial) {
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new IllegalArgumentException("Número de conta obrigatório.");
        }
        if (saldoInicial < 0.0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.numeroConta = numeroConta.trim();
        this.saldo = saldoInicial;
        this.ativa = true;
    }

    public void depositar(double valor) {
        validarContaAtiva();
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do depósito deve ser estritamente positivo.");
        }
        this.saldo += valor;
    }

    public void sacar(double valor) {
        validarContaAtiva();
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do saque deve ser superior a zero.");
        }
        double custoTotal = valor + TARIFA_SAQUE;
        if (custoTotal > this.saldo) {
            throw new IllegalStateException(
                String.format("Saldo insuficiente. Saldo atual: R$ %.2f, Custo total com tarifa: R$ %.2f",
                        this.saldo, custoTotal)
            );
        }
        this.saldo -= custoTotal;
    }

    public void encerrarConta() {
        if (this.saldo > 0.0) {
            throw new IllegalStateException("Contas com saldo pendente não podem ser encerradas.");
        }
        this.ativa = false;
    }

    private void validarContaAtiva() {
        if (!this.ativa) {
            throw new IllegalStateException("Operação recusada: conta inativa no sistema.");
        }
    }

    public String getNumeroConta() { return numeroConta; }
    public double getSaldo() { return saldo; }
    public boolean isAtiva() { return ativa; }
}
