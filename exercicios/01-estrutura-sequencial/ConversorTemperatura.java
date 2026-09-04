import java.util.Locale;
import java.util.Scanner;

public class ConversorTemperatura {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite a temperatura em Celsius (°C): ");
        double c = sc.nextDouble();

        double f = (c * 1.8) + 32;

        System.out.printf("%.1f °C = %.1f °F%n", c, f);
        sc.close();
    }
}
