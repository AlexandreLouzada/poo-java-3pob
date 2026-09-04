import java.util.Locale;
import java.util.Scanner;

public class SituacaoAluno {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite a primeira nota: ");
        double n1 = sc.nextDouble();
        System.out.print("Digite a segunda nota: ");
        double n2 = sc.nextDouble();

        double media = (n1 + n2) / 2.0;

        System.out.printf("Média: %.2f - Situação: ", media);
        if (media >= 7.0) {
            System.out.println("Aprovado");
        } else if (media >= 5.0) {
            System.out.println("Em Recuperação");
        } else {
            System.out.println("Reprovado");
        }

        sc.close();
    }
}
