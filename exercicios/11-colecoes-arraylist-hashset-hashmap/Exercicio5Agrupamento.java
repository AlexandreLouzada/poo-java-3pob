import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Exercicio5Agrupamento {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        List<ProdutoCatalogo> produtos = new ArrayList<>();
        produtos.add(new ProdutoCatalogo("Teclado Mecânico", "Informática", 250.00));
        produtos.add(new ProdutoCatalogo("Mouse sem Fio", "Informática", 80.00));
        produtos.add(new ProdutoCatalogo("O Programador Pragmático", "Livros", 110.00));
        produtos.add(new ProdutoCatalogo("Monitor 27 Pol", "Informática", 1200.00));
        produtos.add(new ProdutoCatalogo("Clean Code", "Livros", 95.00));
        produtos.add(new ProdutoCatalogo("Café Gourmet 500g", "Alimentos", 35.00));

        // Agrupamento manual utilizando Map<String, List<ProdutoCatalogo>>
        Map<String, List<ProdutoCatalogo>> produtosPorCategoria = new HashMap<>();

        for (ProdutoCatalogo p : produtos) {
            produtosPorCategoria
                .computeIfAbsent(p.getCategoria(), k -> new ArrayList<>())
                .add(p);
        }

        // Exibição dos dados agrupados
        for (Map.Entry<String, List<ProdutoCatalogo>> entry : produtosPorCategoria.entrySet()) {
            System.out.println("\n[Categoria: " + entry.getKey() + "]");
            for (ProdutoCatalogo prod : entry.getValue()) {
                System.out.printf("  - %s: R$ %.2f%n", prod.getNome(), prod.getPreco());
            }
        }
    }
}
