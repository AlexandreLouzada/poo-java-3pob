import java.util.Locale;

public class Exercicio1Produto {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Produto p1 = new Produto("Notebook", 3500.0, 5);
        Produto p2 = new Produto("Mouse sem Fio", 80.0);

        System.out.printf("Produto: %s | Total em Estoque: R$ %.2f%n", p1.getNome(), p1.calcularValorTotalEmEstoque());
        System.out.printf("Produto: %s | Total em Estoque: R$ %.2f%n", p2.getNome(), p2.calcularValorTotalEmEstoque());

        p1.setPreco(-50.0);
    }
}
