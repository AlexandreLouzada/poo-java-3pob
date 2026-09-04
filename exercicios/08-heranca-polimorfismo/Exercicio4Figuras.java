import java.util.Locale;

public class Exercicio4Figuras {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        FiguraGeometrica[] figuras = new FiguraGeometrica[] {
            new Quadrado(4.0),
            new RetanguloGeometrico(3.0, 5.0),
            new CirculoGeometrico(2.0)
        };

        for (int i = 0; i < figuras.length; i++) {
            System.out.printf("Figura %d -> Área: %.2f%n", (i + 1), figuras[i].calcularArea());
        }
    }
}
