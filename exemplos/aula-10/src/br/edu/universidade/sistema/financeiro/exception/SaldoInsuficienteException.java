package br.edu.universidade.sistema.financeiro.exception;

// 1. Exceção de Negócio Checada (Checked): O chamador deve tratar
public class SaldoInsuficienteException extends Exception {
    private final double saldoDisponivel;
    private final double valorTentado;

    public SaldoInsuficienteException(String mensagem, double saldoDisponivel, double valorTentado) {
        super(mensagem);
        this.saldoDisponivel = saldoDisponivel;
        this.valorTentado = valorTentado;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public double getValorTentado() {
        return valorTentado;
    }
}
