import java.util.Scanner;

public class ContagemIntervalo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o início do intervalo: ");
        int inicio = sc.nextInt();
        System.out.print("Digite o fim do intervalo: ");
        int fim = sc.nextInt();

        int qtdPares = 0;
        int qtdImpares = 0;

        for (int i = inicio; i <= fim; i++) {
            if (i % 2 == 0) {
                qtdPares++;
            } else {
                qtdImpares++;
            }
        }

        System.out.println("Quantidade de Pares: " + qtdPares);
        System.out.println("Quantidade de Ímpares: " + qtdImpares);

        sc.close();
    }
}
