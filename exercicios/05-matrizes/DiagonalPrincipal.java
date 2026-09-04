import java.util.Locale;
import java.util.Scanner;

public class DiagonalPrincipal {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);
        double[][] matriz = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.printf("Elemento [%d][%d]: ", i, j);
                matriz[i][j] = sc.nextDouble();
            }
        }

        double somaDiagonal = 0.0;
        for (int i = 0; i < 4; i++) {
            somaDiagonal += matriz[i][i]; // Diagonal principal tem linha == coluna
        }

        System.out.printf("Soma da Diagonal Principal: %.2f%n", somaDiagonal);
        sc.close();
    }
}
