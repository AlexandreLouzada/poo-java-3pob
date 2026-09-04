import java.util.Locale;

class Funcionario {
    String nome;
    String cargo;
    double salarioBruto;

    void aplicarAumento(double porcentagem) {
        salarioBruto += salarioBruto * (porcentagem / 100.0);
    }

    double calcularSalarioLiquido(double descontoImposto) {
        return salarioBruto - descontoImposto;
    }

    void exibirDados() {
        System.out.printf("Funcionário: %s | Cargo: %s | Salário Bruto: R$ %.2f%n", nome, cargo, salarioBruto);
    }
}
