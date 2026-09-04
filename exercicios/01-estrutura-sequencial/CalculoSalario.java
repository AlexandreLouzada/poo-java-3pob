import java.util.Locale;
import java.util.Scanner;

public class CalculoSalario {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o valor da hora trabalhada: ");
        double valorHora = sc.nextDouble();
        System.out.print("Digite a quantidade de horas trabalhadas: ");
        double horas = sc.nextDouble();

        double salarioBruto = valorHora * horas;
        double descontoInss = salarioBruto * 0.10;
        double salarioLiquido = salarioBruto - descontoInss;

        System.out.printf("Salário Bruto: R$ %.2f%n", salarioBruto);
        System.out.printf("Desconto INSS (10%%): R$ %.2f%n", descontoInss);
        System.out.printf("Salário Líquido: R$ %.2f%n", salarioLiquido);
        sc.close();
    }
}
