import java.util.Scanner;

public class BuscaLinear {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] vetor = new int[6];

        for (int i = 0; i < vetor.length; i++) {
            System.out.print("Digite o elemento " + i + ": ");
            vetor[i] = sc.nextInt();
        }

        System.out.print("Digite o número que deseja buscar: ");
        int x = sc.nextInt();

        int posicaoEncontrada = -1;
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == x) {
                posicaoEncontrada = i;
                break;
            }
        }

        if (posicaoEncontrada != -1) {
            System.out.println("Elemento " + x + " encontrado na posição (índice) " + posicaoEncontrada + ".");
        } else {
            System.out.println("Elemento " + x + " não encontrado no vetor.");
        }

        sc.close();
    }
}
