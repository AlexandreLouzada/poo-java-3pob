import java.util.Scanner;

public class SomaLinhasMatriz {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] matriz = new int[4][3];
        int[] somas = new int[4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("Elemento [%d][%d]: ", i, j);
                matriz[i][j] = sc.nextInt();
                somas[i] += matriz[i][j];
            }
        }

        System.out.println("\nVetor de somas por linha:");
        for (int i = 0; i < somas.length; i++) {
            System.out.printf("Linha %d: Soma = %d%n", i, somas[i]);
        }

        sc.close();
    }
}
