import java.util.Locale;

public class Exercicio2FolhaPagamento {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Funcionario[] funcionarios = new Funcionario[3];
        funcionarios[0] = new Gerente("Renata", 5000.0, 2000.0);
        funcionarios[1] = new Vendedor("Lucas", 2000.0, 30000.0, 5.0);
        funcionarios[2] = new Funcionario("Pedro", 2500.0);

        double totalFolha = 0.0;

        for (Funcionario f : funcionarios) {
            double sal = f.calcularSalario();
            System.out.printf("Funcionário: %s | Salário Final: R$ %.2f%n", f.getNome(), sal);
            totalFolha += sal;
        }

        System.out.printf("\nTotal da Folha de Pagamento: R$ %.2f%n", totalFolha);
    }
}
