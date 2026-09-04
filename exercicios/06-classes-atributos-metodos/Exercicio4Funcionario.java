import java.util.Locale;

public class Exercicio4Funcionario {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Funcionario func = new Funcionario();
        func.nome = "Ana Souza";
        func.cargo = "Analista de Sistemas";
        func.salarioBruto = 3000.00;

        System.out.println("--- Dados Iniciais ---");
        func.exibirDados();

        func.aplicarAumento(10.0);

        System.out.println("\n--- Após Aumento de 10% ---");
        func.exibirDados();
        System.out.printf("Salário Líquido (com desconto de R$ 300.00): R$ %.2f%n", func.calcularSalarioLiquido(300.00));
    }
}
