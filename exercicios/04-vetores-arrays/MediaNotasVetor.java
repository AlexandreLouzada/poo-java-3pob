import java.util.Locale;
import java.util.Scanner;

public class MediaNotasVetor {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);
        double[] notas = new double[8];
        double soma = 0.0;

        for (int i = 0; i < notas.length; i++) {
            System.out.print("Nota do aluno " + (i + 1) + ": ");
            notas[i] = sc.nextDouble();
            soma += notas[i];
        }

        double media = soma / notas.length;
        System.out.printf("Média da turma: %.2f%n", media);

        System.out.print("Notas acima da média: ");
        for (int i = 0; i < notas.length; i++) {
            if (notas[i] > media) {
                System.out.print(notas[i] + " ");
            }
        }
        System.out.println();

        sc.close();
    }
}
