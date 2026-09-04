import java.util.Locale;
import java.util.Scanner;

public class SomatorioMedia {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        int soma = 0;
        int quantidade = 0;

        System.out.print("Digite um número (negativo para parar): ");
        int valor = sc.nextInt();

        while (valor >= 0) {
            soma += valor;
            quantidade++;
            System.out.print("Digite o próximo número: ");
            valor = sc.nextInt();
        }

        if (quantidade > 0) {
            double media = (double) soma / quantidade;
            System.out.printf("Quantidade: %d | Soma: %d | Média: %.2f%n", quantidade, soma, media);
        } else {
            System.out.println("Nenhum número positivo foi digitado.");
        }

        sc.close();
    }
}
