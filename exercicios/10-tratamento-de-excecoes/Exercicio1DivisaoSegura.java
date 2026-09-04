import java.util.InputMismatchException;
import java.util.Scanner;

public class Exercicio1DivisaoSegura {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Digite o primeiro número inteiro (dividendo): ");
            int a = sc.nextInt();

            System.out.print("Digite o segundo número inteiro (divisor): ");
            int b = sc.nextInt();

            int resultado = a / b;
            System.out.println("Resultado da divisão inteira: " + resultado);

        } catch (ArithmeticException e) {
            System.out.println("Erro: Não é possível dividir um número por zero.");
        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Digite apenas números inteiros.");
        } finally {
            System.out.println("Operação finalizada.");
            sc.close();
        }
    }
}
