import java.util.Scanner;

public class MaiorMenorVetor {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] vetor = new int[10];

        for (int i = 0; i < vetor.length; i++) {
            System.out.print("Digite o elemento " + i + ": ");
            vetor[i] = sc.nextInt();
        }

        int maior = vetor[0], posMaior = 0;
        int menor = vetor[0], posMenor = 0;

        for (int i = 1; i < vetor.length; i++) {
            if (vetor[i] > maior) {
                maior = vetor[i];
                posMaior = i;
            }
            if (vetor[i] < menor) {
                menor = vetor[i];
                posMenor = i;
            }
        }

        System.out.println("Maior: " + maior + " (índice " + posMaior + ")");
        System.out.println("Menor: " + menor + " (índice " + posMenor + ")");

        sc.close();
    }
}
