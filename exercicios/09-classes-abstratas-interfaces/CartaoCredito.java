import java.util.Locale;

class CartaoCredito implements MetodoPagamento {
    private String numeroCartao;
    private double limite;

    public CartaoCredito(String numeroCartao, double limite) {
        this.numeroCartao = numeroCartao;
        this.limite = limite;
    }

    @Override
    public void processarPagamento(double valor) {
        if (valor <= limite) {
            limite -= valor;
            System.out.printf("Pagamento de R$ %.2f processado via Cartão de Crédito.%n", valor);
        } else {
            System.out.println("Transação recusada: Limite insuficiente no Cartão de Crédito.");
        }
    }

    @Override
    public String obterDetalhes() {
        return "Cartão de Crédito terminado em " + numeroCartao.substring(numeroCartao.length() - 4);
    }
}
