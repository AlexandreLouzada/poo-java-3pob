import java.util.Locale;
import java.util.Scanner;

public class Exercicio2Circulo {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        Circulo circ = new Circulo();

        System.out.print("Digite o raio do círculo: ");
        circ.raio = sc.nextDouble();

        System.out.printf("Área: %.2f | Perímetro: %.2f%n", circ.calcularArea(), circ.calcularPerimetro());

        sc.close();
    }
}
