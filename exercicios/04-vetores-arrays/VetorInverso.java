import java.util.Scanner;

public class VetorInverso {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] vetor = new int[5];

        for (int i = 0; i < vetor.length; i++) {
            System.out.print("Elemento [" + i + "]: ");
            vetor[i] = sc.nextInt();
        }

        System.out.print("Vetor invertido: ");
        for (int i = vetor.length - 1; i >= 0; i--) {
            System.out.print(vetor[i] + (i > 0 ? ", " : ""));
        }
        System.out.println();

        sc.close();
    }
}
