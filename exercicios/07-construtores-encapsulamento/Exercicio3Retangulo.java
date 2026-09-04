import java.util.Locale;

public class Exercicio3Retangulo {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Retangulo r1 = new Retangulo(4.0, 5.0);
        System.out.printf("Retângulo 1 -> Área: %.2f | Perímetro: %.2f%n", r1.calcularArea(), r1.calcularPerimetro());

        Retangulo r2 = new Retangulo(-3.0, 0.0);
        System.out.printf("Retângulo 2 (corrigido) -> Área: %.2f | Perímetro: %.2f%n", r2.calcularArea(), r2.calcularPerimetro());
    }
}
