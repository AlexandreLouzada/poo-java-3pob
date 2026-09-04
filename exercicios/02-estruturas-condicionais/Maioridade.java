import java.util.Scanner;

public class Maioridade {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o ano de nascimento: ");
        int anoNascimento = sc.nextInt();
        System.out.print("Digite o ano atual: ");
        int anoAtual = sc.nextInt();

        int idade = anoAtual - anoNascimento;

        if (idade >= 18) {
            System.out.println("Idade: " + idade + " anos. Maior de idade.");
        } else {
            System.out.println("Idade: " + idade + " anos. Menor de idade.");
        }

        sc.close();
    }
}
