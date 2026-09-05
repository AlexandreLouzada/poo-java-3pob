package br.edu.universidade.sistema.credito;

public class PropostaCredito {
    private final String cpfCliente;
    private final double rendaMensal;
    private final double valorSolicitado;
    private final int quantidadeMeses;

    public PropostaCredito(String cpfCliente, double rendaMensal, double valorSolicitado, int quantidadeMeses) {
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente nao pode ser nulo ou vazio.");
        }
        if (rendaMensal <= 0.0) {
            throw new IllegalArgumentException("Renda mensal deve ser maior que zero: " + rendaMensal);
        }
        if (valorSolicitado <= 0.0) {
            throw new IllegalArgumentException("Valor solicitado deve ser maior que zero: " + valorSolicitado);
        }
        if (quantidadeMeses <= 0) {
            throw new IllegalArgumentException("Quantidade de meses deve ser maior que zero: " + quantidadeMeses);
        }
        this.cpfCliente = cpfCliente;
        this.rendaMensal = rendaMensal;
        this.valorSolicitado = valorSolicitado;
        this.quantidadeMeses = quantidadeMeses;
    }

    public double calcularValorParcelaMensal() {
        double montante = this.valorSolicitado * 1.05;
        return montante / this.quantidadeMeses;
    }

    public boolean isAprovada() {
        double parcela = calcularValorParcelaMensal();
        return parcela <= 0.30 * this.rendaMensal;
    }

    public String getCpfCliente() { return cpfCliente; }
    public double getRendaMensal() { return rendaMensal; }
    public double getValorSolicitado() { return valorSolicitado; }
    public int getQuantidadeMeses() { return quantidadeMeses; }
}