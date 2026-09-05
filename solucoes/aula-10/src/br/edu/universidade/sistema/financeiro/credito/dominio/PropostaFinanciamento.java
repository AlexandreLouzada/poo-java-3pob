package br.edu.universidade.sistema.financeiro.credito.dominio;

import br.edu.universidade.sistema.financeiro.credito.exception.LimiteCreditoExcedidoException;
import br.edu.universidade.sistema.financeiro.credito.exception.ScoreSerasaInvalidoException;

public class PropostaFinanciamento {

    private static final double TAXA_FIXA_ENCARGOS = 0.10;

    private String cpfCliente;
    private double rendaMensal;
    private double valorEmprestimo;
    private int quantidadeMeses;

    public PropostaFinanciamento(String cpfCliente, double rendaMensal, double valorEmprestimo, int quantidadeMeses) {
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente nao pode ser vazio.");
        }
        if (rendaMensal <= 0.0) {
            throw new IllegalArgumentException("Renda mensal deve ser positiva: " + rendaMensal);
        }
        if (valorEmprestimo <= 0.0) {
            throw new IllegalArgumentException("Valor do emprestimo deve ser positivo: " + valorEmprestimo);
        }
        if (quantidadeMeses <= 0) {
            throw new IllegalArgumentException("Quantidade de meses deve ser positiva: " + quantidadeMeses);
        }
        this.cpfCliente = cpfCliente;
        this.rendaMensal = rendaMensal;
        this.valorEmprestimo = valorEmprestimo;
        this.quantidadeMeses = quantidadeMeses;
    }

    public double calcularParcela() {
        double montante = this.valorEmprestimo * (1.0 + TAXA_FIXA_ENCARGOS);
        return montante / this.quantidadeMeses;
    }

    public void validarAprovacao(int scoreSerasa) throws LimiteCreditoExcedidoException {
        if (scoreSerasa < 0 || scoreSerasa > 1000) {
            throw new ScoreSerasaInvalidoException(
                    "Score Serasa fora do intervalo permitido (0 a 1000): " + scoreSerasa, scoreSerasa);
        }
        if (scoreSerasa < 400) {
            throw new ScoreSerasaInvalidoException(
                    "Proposta recusada: score insuficiente para concessao", scoreSerasa);
        }

        double parcela = calcularParcela();
        if (parcela > 0.30 * this.rendaMensal) {
            throw new LimiteCreditoExcedidoException(this.rendaMensal, parcela);
        }
    }

    public String getCpfCliente() { return cpfCliente; }
    public double getRendaMensal() { return rendaMensal; }
    public double getValorEmprestimo() { return valorEmprestimo; }
    public int getQuantidadeMeses() { return quantidadeMeses; }
}