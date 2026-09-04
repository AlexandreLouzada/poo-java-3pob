import java.util.Scanner;

public class Triangulos {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o lado A: ");
        int a = sc.nextInt();
        System.out.print("Digite o lado B: ");
        int b = sc.nextInt();
        System.out.print("Digite o lado C: ");
        int c = sc.nextInt();

        // Verificação de existência
        if ((a + b > c) && (a + c > b) && (b + c > a)) {
            if (a == b && b == c) {
                System.out.println("Triângulo Equilátero");
            } else if (a == b || a == c || b == c) {
                System.out.println("Triângulo Isósceles");
            } else {
                System.out.println("Triângulo Escaleno");
            }
        } else {
            System.out.println("Os lados informados não formam um triângulo.");
        }

        sc.close();
    }
}
