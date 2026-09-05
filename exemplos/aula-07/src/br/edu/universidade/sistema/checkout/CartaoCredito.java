package br.edu.universidade.sistema.checkout;

// 3. Subclasse Concreta 1: Cartão de Crédito
public class CartaoCredito extends MeioPagamento {
    private final double taxaPercentualOperadora;
    private double limiteDisponivel;

    public CartaoCredito(String codigoTransacao, double taxaPercentualOperadora, double limiteDisponivel) {
        super(codigoTransacao); // Obrigatório: Primeira linha do construtor
        this.taxaPercentualOperadora = taxaPercentualOperadora;
        this.limiteDisponivel = limiteDisponivel;
    }

    @Override
    public boolean autorizar(double valor) {
        double custoTotal = valor * (1 + (taxaPercentualOperadora / 100.0));
        if (valor > 0.0 && this.limiteDisponivel >= custoTotal) {
            this.limiteDisponivel -= custoTotal;
            this.valorAutorizado = custoTotal;
            return true;
        }
        return false;
    }

    @Override
    public String emitirComprovante() {
        return String.format("TX [%s] - CARTAO: Autorizado R$ %.2f (Taxa: %.1f%%) | Limite Restante: R$ %.2f",
                codigoTransacao, valorAutorizado, taxaPercentualOperadora, limiteDisponivel);
    }
}
