import java.util.Scanner;

public class MaiorElementoMatriz {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] matriz = new int[3][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.printf("Elemento [%d][%d]: ", i, j);
                matriz[i][j] = sc.nextInt();
            }
        }

        int maior = matriz[0][0];
        int linMaior = 0, colMaior = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (matriz[i][j] > maior) {
                    maior = matriz[i][j];
                    linMaior = i;
                    colMaior = j;
                }
            }
        }

        System.out.printf("Maior valor: %d | Posição: Linha %d, Coluna %d%n", maior, linMaior, colMaior);
        sc.close();
    }
}
