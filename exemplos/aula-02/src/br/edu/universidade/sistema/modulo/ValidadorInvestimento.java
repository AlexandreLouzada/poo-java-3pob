package br.edu.universidade.sistema.modulo;

import java.util.Locale;
import java.util.Scanner;

public class ValidadorInvestimento {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scanner = new Scanner(System.in);
        double aporte = 0.0;

        // Validação robusta de entrada positiva com do-while
        do {
            System.out.print("Informe o aporte inicial (> 0): R$ ");
            while (!scanner.hasNextDouble()) {
                System.out.println("Entrada inválida! Digite um valor numérico.");
                scanner.next(); // Descarta entrada inválida
            }
            aporte = scanner.nextDouble();
        } while (aporte <= 0);

        System.out.printf("Aporte validado com sucesso: R$ %.2f%n", aporte);
        scanner.close();
    }
}
