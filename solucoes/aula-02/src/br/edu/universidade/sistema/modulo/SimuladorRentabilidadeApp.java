package br.edu.universidade.sistema.modulo;

import java.util.Locale;
import java.util.Scanner;

public class SimuladorRentabilidadeApp {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scanner = new Scanner(System.in);

        double capital = lerCapital(scanner);
        int meses = lerPrazoMeses(scanner);
        int perfil = lerPerfilInvestidor(scanner);

        double taxaMensal = switch (perfil) {
            case 1 -> 0.007;
            case 2 -> 0.011;
            case 3 -> 0.016;
            default -> 0.0;
        };

        String nomePerfil = switch (perfil) {
            case 1 -> "Conservador";
            case 2 -> "Moderado";
            case 3 -> "Arrojado";
            default -> "Indefinido";
        };

        System.out.printf("%n--- Projecao Mensal [Perfil %d - %s | Taxa: %.1f%% a.m.] ---%n",
                perfil, nomePerfil, (taxaMensal * 100));

        double saldo = capital;
        for (int mes = 1; mes <= meses; mes++) {
            saldo = saldo * (1 + taxaMensal);
            System.out.printf("Mes %02d -> Saldo acumulado: R$ %.2f%n", mes, saldo);
        }

        System.out.printf("%nRentabilidade final do periodo: R$ %.2f (ganho de R$ %.2f)%n",
                saldo, (saldo - capital));
        scanner.close();
    }

    private static double lerCapital(Scanner scanner) {
        double capital = 0.0;
        System.out.print("Informe o capital principal (> 0): R$ ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida! Digite um valor numérico.");
            scanner.next();
        }
        capital = scanner.nextDouble();
        while (capital <= 0.0) {
            System.out.print("Capital deve ser maior que zero. Informe novamente: R$ ");
            while (!scanner.hasNextDouble()) {
                System.out.println("Entrada inválida! Digite um valor numérico.");
                scanner.next();
            }
            capital = scanner.nextDouble();
        }
        return capital;
    }

    private static int lerPrazoMeses(Scanner scanner) {
        int meses = 0;
        System.out.print("Informe o prazo da aplicacao em meses (1 a 60): ");
        while (true) {
            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida! Digite um número inteiro de meses.");
                scanner.next();
            }
            meses = scanner.nextInt();
            if (meses >= 1 && meses <= 60) {
                break;
            }
            System.out.print("Prazo inválido! Informe um valor entre 1 e 60 meses: ");
        }
        return meses;
    }

    private static int lerPerfilInvestidor(Scanner scanner) {
        int perfil = 0;
        System.out.print("Informe o perfil do investidor (1=Conservador, 2=Moderado, 3=Arrojado): ");
        while (true) {
            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida! Digite 1, 2 ou 3.");
                scanner.next();
            }
            perfil = scanner.nextInt();
            if (perfil >= 1 && perfil <= 3) {
                break;
            }
            System.out.print("Perfil inválido! Informe 1, 2 ou 3: ");
        }
        return perfil;
    }
}