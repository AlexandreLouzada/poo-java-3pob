import java.util.Locale;

interface MetodoPagamento {
    void processarPagamento(double valor);
    String obterDetalhes();
}
