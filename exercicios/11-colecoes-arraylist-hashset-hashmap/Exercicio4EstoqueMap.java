import java.util.HashMap;
import java.util.Map;

public class Exercicio4EstoqueMap {
    public static void main(String[] args) {
        ControleEstoque estoque = new ControleEstoque();

        estoque.cadastrarOuAdicionar("PROD-01", 10);
        estoque.cadastrarOuAdicionar("PROD-02", 3);
        estoque.cadastrarOuAdicionar("PROD-03", 15);

        estoque.darBaixa("PROD-01", 6);
        estoque.darBaixa("PROD-02", 5); // Tentativa que excede o saldo

        estoque.relatorioEstoqueBaixo(5);
    }
}
