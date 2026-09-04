import java.util.Locale;

public class Exercicio5Funcionario {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Funcionario func = new Funcionario("Beatriz Lima", "MAT-4412", 4500.0);
        func.exibirDados();

        func.setSalario(4200.0);
        func.setSalario(5200.0);

        func.exibirDados();
    }
}
