import java.util.Scanner;

public class ConversorTempo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o total de segundos: ");
        int totalSegundos = sc.nextInt();

        int horas = totalSegundos / 3600;
        int resto = totalSegundos % 3600;
        int minutos = resto / 60;
        int segundos = resto % 60;

        System.out.printf("%d hora(s), %d minuto(s) e %d segundo(s)%n", horas, minutos, segundos);
        sc.close();
    }
}
