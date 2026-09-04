import java.util.Locale;

class Pix implements MetodoPagamento {
    private String chavePix;

    public Pix(String chavePix) {
        this.chavePix = chavePix;
    }

    @Override
    public void processarPagamento(double valor) {
        System.out.printf("Pagamento de R$ %.2f processado via Pix.%n", valor);
    }

    @Override
    public String obterDetalhes() {
        return "Chave Pix: " + chavePix;
    }
}
