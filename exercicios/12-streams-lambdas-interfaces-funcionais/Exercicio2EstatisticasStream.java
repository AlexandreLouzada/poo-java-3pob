import java.util.stream.Stream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Exercicio2EstatisticasStream {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        List<ProdutoStream> produtos = Arrays.asList(
                new ProdutoStream("Notebook", 3500.0),
                new ProdutoStream("Mouse", 80.0),
                new ProdutoStream("Teclado", 220.0),
                new ProdutoStream("Monitor", 1200.0),
                new ProdutoStream("Headset", 300.0),
                new ProdutoStream("Webcam", 180.0)
        );

        // 1. Valor total somado
        double total = produtos.stream()
                .mapToDouble(ProdutoStream::getPreco)
                .sum();

        // 2. Média de preço
        double media = produtos.stream()
                .mapToDouble(ProdutoStream::getPreco)
                .average()
                .orElse(0.0);

        // 3. Produto mais caro
        Optional<ProdutoStream> maisCaro = produtos.stream()
                .max(Comparator.comparingDouble(ProdutoStream::getPreco));

        System.out.printf("Valor Total: R$ %.2f%n", total);
        System.out.printf("Preço Médio: R$ %.2f%n", media);
        maisCaro.ifPresent(p -> System.out.printf("Produto mais caro: %s (R$ %.2f)%n", p.getNome(), p.getPreco()));
    }
}

