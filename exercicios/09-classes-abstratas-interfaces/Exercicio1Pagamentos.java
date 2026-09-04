import java.util.Locale;

public class Exercicio1Pagamentos {
    public static void finalizarCompra(MetodoPagamento metodo, double total) {
        System.out.println("Iniciando transação...");
        System.out.println("Método: " + metodo.obterDetalhes());
        metodo.processarPagamento(total);
        System.out.println("-------------------------");
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        MetodoPagamento cartao = new CartaoCredito("1234-5678-9012-3456", 1000.0);
        MetodoPagamento pix = new Pix("pix@banco.com");

        finalizarCompra(pix, 150.0);
        finalizarCompra(cartao, 800.0);
        finalizarCompra(cartao, 300.0); // Passará do limite
    }
}
