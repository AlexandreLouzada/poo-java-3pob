import java.util.Scanner;

public class ValidacaoSenha {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        final int SENHA_CORRETA = 2026;

        System.out.print("Digite a senha: ");
        int senha = sc.nextInt();

        while (senha != SENHA_CORRETA) {
            System.out.println("Senha Incorreta! Tente novamente.");
            System.out.print("Digite a senha: ");
            senha = sc.nextInt();
        }

        System.out.println("Acesso Permitido!");
        sc.close();
    }
}
