import java.util.Scanner;

public class MatrizTransposta {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] original = new int[2][3];
        int[][] transposta = new int[3][2];

        System.out.println("Preenchendo a matriz original (2x3):");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf("Elemento [%d][%d]: ", i, j);
                original[i][j] = sc.nextInt();
                transposta[j][i] = original[i][j]; // Atribuição direta da transposição
            }
        }

        System.out.println("\nMatriz Transposta (3x2):");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.printf("%4d", transposta[i][j]);
            }
            System.out.println();
        }

        sc.close();
    }
}
