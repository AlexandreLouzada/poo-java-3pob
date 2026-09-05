package br.edu.universidade.sistema.financeiro.credito.exception;

public class LimiteCreditoExcedidoException extends Exception {

    private final double rendaMensal;
    private final double valorParcelaPretendida;
    private final double percentualComprometimento;

    public LimiteCreditoExcedidoException(double rendaMensal, double valorParcelaPretendida) {
        super("Parcela de R$ " + String.format("%.2f", valorParcelaPretendida)
                + " compromete mais de 30% da renda mensal de R$ "
                + String.format("%.2f", rendaMensal));
        this.rendaMensal = rendaMensal;
        this.valorParcelaPretendida = valorParcelaPretendida;
        this.percentualComprometimento = rendaMensal > 0.0 ? (valorParcelaPretendida / rendaMensal) : 0.0;
    }

    public double getRendaMensal() {
        return rendaMensal;
    }

    public double getValorParcelaPretendida() {
        return valorParcelaPretendida;
    }

    public double getPercentualComprometimento() {
        return percentualComprometimento;
    }
}