import java.util.List;
import java.util.Locale;

public class Exercicio4UpperBoundedWildcard {

    // Producer Extends: a lista produz dados do tipo Figura para leitura segura
    public static double somarAreas(List<? extends Figura> listaFiguras) {
        double total = 0.0;
        for (Figura fig : listaFiguras) {
            total += fig.calcularArea(); // Leitura segura
        }
        // listaFiguras.add(new QuadradoFigura(2.0)); // Erro: proibido inserir via ? extends
        return total;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        List<QuadradoFigura> quadrados = List.of(new QuadradoFigura(2.0), new QuadradoFigura(3.0));
        List<CirculoFigura> circulos = List.of(new CirculoFigura(2.0), new CirculoFigura(1.5));

        System.out.printf("Soma Áreas Quadrados: %.2f%n", somarAreas(quadrados));
        System.out.printf("Soma Áreas Círculos: %.2f%n", somarAreas(circulos));
    }
}
